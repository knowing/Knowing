package de.sendsor.accelerationSensor.algorithm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.elki.algorithm.clustering.KMeans;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.cluster.Cluster;
import de.lmu.ifi.dbs.elki.data.model.MeanModel;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.DatabaseObjectMetadata;
import de.lmu.ifi.dbs.elki.database.SequentialDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.distance.distancefunction.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;
import de.lmu.ifi.dbs.utilities.Arrays2;
import de.lmu.ifi.dbs.utilities.Collections2;
import de.lmu.ifi.dbs.utilities.distances.EuclideanSquared;
import de.sendsor.accelerationSensor.util.LabeledDoubleFeature;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.3
 */
public class ClusterContainer implements Serializable {

	private static final long serialVersionUID = 6080707413886696936L;
	
	private transient static final Logger log = Logger.getLogger(PieAnalyzer.class.getName());
	
	public static final int KMEANS_K = 1000;
	public static final int KMEANS_MAX_ITERATION = 20;
	public static final int KERNEL_SIZE = 7;
	public static final int MIN_INSTANCES_PER_CLUSTER = 5;

	private List<MyCluster> clusters;

	public HashMap<String, Double> evaluate(List<LabeledDoubleFeature> features) {

		final EuclideanSquared dist = new EuclideanSquared();
		
		//Categories == Labels
		List<String> labels = new ArrayList<String>();
		for (LabeledDoubleFeature c : features) 
			if (!labels.contains(c.getLabel())) 
				labels.add(c.getLabel());


		// classify
		int[][] confusionMatrix = new int[labels.size()][labels.size()];
		for (LabeledDoubleFeature fv : features) {
			// distance to clusters
			List<SimpleEntry<Double, MyCluster>> clusterDist = new ArrayList<SimpleEntry<Double, MyCluster>>();
			for (MyCluster c : clusters) {
				clusterDist.add(new SimpleEntry<Double, MyCluster>(dist.dist(c.getCentroid(), fv.getValues()), c));
			}
			Collections.sort(clusterDist, new KeyComp());

			// max Dist
			final double maxDist = clusterDist.get(clusterDist.size() - 1).getKey();
			HashMap<String, List<Double>> probabilityMap = new HashMap<String, List<Double>>();
			for (int i = 0; i < clusterDist.size(); i++) {
				SimpleEntry<Double, MyCluster> e = clusterDist.get(i);
				double weight = 1 - (e.getKey() / maxDist);
				MyCluster cluster = e.getValue();
				// add weighted labels to probability map
				addToMap(probabilityMap, cluster.getLabels(), weight);
			}

			HashMap<String, Double> finalMap = getFinalWeights(probabilityMap, clusters.size());
			String classifiedAs = getLabelWithHighestPropability(finalMap);

			final int classifiedAsIndex = labels.indexOf(classifiedAs);
			final int trueIndex = labels.indexOf(fv.getLabel());
			confusionMatrix[trueIndex][classifiedAsIndex] += 1;
		}

		// print result
		double avgHitRate = 0;
		String s = "";
		for (int i = 0; i < confusionMatrix.length; i++) {
			double trueHits = confusionMatrix[i][i] * 1d / Arrays2.sum(confusionMatrix[i]);
			avgHitRate += trueHits / confusionMatrix.length;

			s += labels.get(i) + ";";
			s += Arrays2.join(confusionMatrix[i], ";");
			s += String.format(";%f", trueHits);
			s += "\n";
		}
		s += ";" + Collections2.joinToString(labels, ";");
		s += String.format(";%f", avgHitRate);
		//log.info("-----------\n" + s);

		return null;
	}

	public List<MyCluster> cluster(List<LabeledDoubleFeature> train) throws UnableToComplyException {
		// fill db and map doublevectors to my vectors
		HashMap<DoubleVector, LabeledDoubleFeature> vecMap = new HashMap<DoubleVector, LabeledDoubleFeature>();
		Database<DatabaseObject> db = new SequentialDatabase<DatabaseObject>();
		final DatabaseObjectMetadata assoc = new DatabaseObjectMetadata();
		for (LabeledDoubleFeature v : train) {
			DoubleVector elkivec = new DoubleVector(v.getValues());
			vecMap.put(elkivec, v);
			db.insert(new Pair<DatabaseObject, DatabaseObjectMetadata>(elkivec,	assoc));
		}

		// init KMeans

		log.info("k=" + KMEANS_K + "; maxiterations=" + KMEANS_MAX_ITERATION);
		KMeans kmeans = new KMeans(EuclideanDistanceFunction.STATIC, KMEANS_K,	KMEANS_MAX_ITERATION);

		// do clustering
		List<MyCluster> myClusters = new ArrayList<MyCluster>();
		Clustering<MeanModel<DoubleVector>> result = (Clustering<MeanModel<DoubleVector>>) kmeans.run(db);
		List<Cluster<MeanModel<DoubleVector>>> clusters = result.getAllClusters();
		for (Cluster<MeanModel<DoubleVector>> cluster : clusters) {
			MeanModel<DoubleVector> mm = cluster.getModel();
			double[] centroid = mm.getMean().getValues();
			List<LabeledDoubleFeature> clusterVectors = new ArrayList<LabeledDoubleFeature>();
			DoubleVector elkivector;
			for (DBID id : cluster.getIDs()) {
				elkivector = (DoubleVector) db.get(id);
				clusterVectors.add(vecMap.get(elkivector));
			}

			MyCluster newCluster = buildCluster(centroid, clusterVectors);
			if (newCluster.getNumChildren() > MIN_INSTANCES_PER_CLUSTER) {
				myClusters.add(newCluster);
				
				//Trained Clusters
				this.clusters.add(newCluster);
			}
		}

		log.info("Found " + myClusters.size() + " clusters with > "	+ MIN_INSTANCES_PER_CLUSTER + " members");
		return myClusters;
	}
	
	private MyCluster buildCluster(double[] centroid, List<LabeledDoubleFeature> clusterVectors) {
		// count labels in this cluster
		HashMap<String, Double> labels = new HashMap<String, Double>();
		for (LabeledDoubleFeature v : clusterVectors) {
			String label = v.getLabel();
			if (!labels.containsKey(label)) {
				labels.put(label, 1d);
			} else {
				Double count = labels.get(label);
				labels.put(label, count + 1);
			}
		}

		// label probability between 0-1
		final int clusterCount = clusterVectors.size();
		for (String key : labels.keySet()) {
			labels.put(key, labels.get(key) / clusterCount);
		}

		return new MyCluster(centroid, labels, clusterCount);
	}
	
	
	private HashMap<String, Double> getFinalWeights(HashMap<String, List<Double>> probabilityMap, int numClusters) {
		HashMap<String, Double> map = new HashMap<String, Double>();
		for (Entry<String, List<Double>> entry : probabilityMap.entrySet()) {
			map.put(entry.getKey(), Collections2.sum(entry.getValue()) / numClusters);
		}
		return map;
	}

	private void addToMap(HashMap<String, List<Double>> probabilityMap,
			HashMap<String, Double> labels, double weight) {
		for (Entry<String, Double> entry : labels.entrySet()) {
			String key = entry.getKey();
			if (!probabilityMap.containsKey(key)) {
				probabilityMap.put(key, new ArrayList<Double>());
			}

			List<Double> list = probabilityMap.get(key);
			list.add(entry.getValue() * weight);
		}
	}

	private String getLabelWithHighestPropability(
			HashMap<String, Double> finalMap) {
		List<Entry<String, Double>> l = new ArrayList<Entry<String, Double>>(
				finalMap.entrySet());
		Collections.sort(l, new Comparator<Entry<String, Double>>() {

			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		return l.get(l.size() - 1).getKey();
	}

	// *************** Serialization ****************//

	public void store(OutputStream out) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(out));
		objectOutputStream.writeObject(this);
		objectOutputStream.flush();
		objectOutputStream.close();
	}

	public ClusterContainer load(InputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(in));
		Object object = objectInputStream.readObject();
		if (object instanceof ClusterContainer)
			return (ClusterContainer) object;
		return null;
	}

}
