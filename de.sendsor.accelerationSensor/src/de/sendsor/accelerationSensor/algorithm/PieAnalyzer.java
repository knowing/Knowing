package de.sendsor.accelerationSensor.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;

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
import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.AbstractAlgorithm;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.utilities.Arrays2;
import de.lmu.ifi.dbs.utilities.Collections2;
import de.lmu.ifi.dbs.utilities.Math2;
import de.lmu.ifi.dbs.utilities.distances.EuclideanSquared;
import de.sendsor.accelerationSensor.util.LabeledDoubleFeature;
import de.sendsor.accelerationSensor.util.Utils;

/**
 * Analyze the data via a simple clustering algorithm.
 * 
 * @author Nepomuk Seiler
 * @version 0.0
 */
public class PieAnalyzer extends AbstractAlgorithm<Data> {

	public static final int RANDOM_INIT = 5;
	static final Logger log = Logger.getLogger(PieAnalyzer.class.getName());
	static final int KMEANS_K = 1000;
	static final int KMEANS_MAX_ITERATION = 20;
	static final int KERNEL_SIZE = 7;
	static final int MIN_INSTANCES_PER_CLUSTER = 5;
	
	public static final String NAME = "Kuchendiagramm";

	@Override
	public IAnalyzedData process(Object data) {
		// Can only handle Data.class
		Assert.isTrue(data instanceof Data[]);

		List<LabeledDoubleFeature> features = new ArrayList<LabeledDoubleFeature>();
		List<LabeledDoubleFeature> rawVectors = Utils.readRawFeaturesFromData((Data[]) data);
		List<LabeledDoubleFeature> converted = raw2Features(rawVectors);
		log.info("read " + rawVectors.size() + ", compactified to "	+ converted.size());
		features.addAll(converted);

		log.info("features: " + features.size());
		// writeToCSV(features, new File("./sendsor.csv"));

		// split into train/test
		List<LabeledDoubleFeature> train = new ArrayList<LabeledDoubleFeature>();
		List<LabeledDoubleFeature> test = new ArrayList<LabeledDoubleFeature>();
		splitTrainTest(features, train, test);
		features = null;

		// cluster training data
		List<MyCluster> clusters = null;
		try {
			clusters = cluster(train);
			for (MyCluster c : clusters) { // print clusters
				log.info(c.toString());
			}
		} catch (UnableToComplyException e) {
			e.printStackTrace();
		}

		// test
		test(clusters, test);
		return new PieAnalyzerData();
	}
	
	@Override
	public IAnalyzedData process(Object data, IAnalyzedData analyzedData) {
		//TODO Implement Process-Chaining
		return null;
	}

	private List<LabeledDoubleFeature> raw2Features(List<LabeledDoubleFeature> raw) {
		final int window = 40;
		final int stepSize = 40;
		final int dim = raw.get(0).getValues().length;

		List<LabeledDoubleFeature> compact = new ArrayList<LabeledDoubleFeature>();
		double[] v;
		for (int i = 0; i < raw.size() - window; i += stepSize) {
			// calc mean
			double[] mean = new double[dim];
			for (int j = i; j < i + window; j++) {
				v = raw.get(j).getValues();
				Arrays2.add(mean, v);
			}
			Arrays2.div(mean, window);

			// calc var
			double[] variance = new double[dim];
			for (int j = i; j < i + window; j++) {
				v = raw.get(j).getValues();
				for (int k = 0; k < dim; k++) {
					variance[k] += Math2.pow(v[k] - mean[k], 2);
				}
			}
			for (int j = 0; j < dim; j++) {
				variance[j] = Math.sqrt(variance[j] / window);
			}

			// concatenate and build new vector
			double[] newValues = Arrays2.append(mean, variance);
			compact.add(new LabeledDoubleFeature(newValues, raw.get(0)
					.getLabel()));
		}

		return compact;
	}

	private void splitTrainTest(List<LabeledDoubleFeature> features,
			List<LabeledDoubleFeature> train, List<LabeledDoubleFeature> test) {
		final int frac = 10; // fraction of test data
		log.info("split into train " + (100 - frac) + "% / test " + frac + "%");
		Random r = new Random(RANDOM_INIT);
		for (int i = 0; i < features.size(); i++) {
			if (r.nextInt(100) < frac) {
				test.add(features.get(i));
			} else {
				train.add(features.get(i));
			}
		}
	}

	private List<MyCluster> cluster(List<LabeledDoubleFeature> train)
			throws UnableToComplyException {
		log.info("KMeans clustering");

		// fill db and map doublevectors to my vectors
		HashMap<DoubleVector, LabeledDoubleFeature> vecMap = new HashMap<DoubleVector, LabeledDoubleFeature>();
		Database<DatabaseObject> db = new SequentialDatabase<DatabaseObject>();
		final DatabaseObjectMetadata assoc = new DatabaseObjectMetadata();
		for (LabeledDoubleFeature v : train) {
			DoubleVector elkivec = new DoubleVector(v.getValues());
			vecMap.put(elkivec, v);
			db.insert(new Pair<DatabaseObject, DatabaseObjectMetadata>(elkivec,
					assoc));
		}

		// init KMeans

		log.info("k=" + KMEANS_K + "; maxiterations=" + KMEANS_MAX_ITERATION);
		KMeans kmeans = new KMeans(EuclideanDistanceFunction.STATIC, KMEANS_K,	KMEANS_MAX_ITERATION);

		// do clustering
		List<MyCluster> myClusters = new ArrayList<MyCluster>();
		Clustering<MeanModel<DoubleVector>> result = (Clustering<MeanModel<DoubleVector>>) kmeans.run(db);
		List<Cluster<MeanModel<DoubleVector>>> clusters = result
				.getAllClusters();
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
			}
		}

		log.info("Found " + myClusters.size() + " clusters with > "
				+ MIN_INSTANCES_PER_CLUSTER + " members");
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

	private void test(List<MyCluster> clusters, List<LabeledDoubleFeature> test) {
		final EuclideanSquared dist = new EuclideanSquared();
		List<String> labels = new ArrayList<String>();
		for (LabeledDoubleFeature c : test) {
			if (!labels.contains(c.getLabel())) {
				labels.add(c.getLabel());
			}
		}

		// classify
		int[][] confusionMatrix = new int[labels.size()][labels.size()];
		for (LabeledDoubleFeature fv : test) {
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

			HashMap<String, Double> finalMap = getFinalWeights(probabilityMap,
					clusters.size());
			String classifiedAs = getLabelWithHighestPropability(finalMap);

			final int classifiedAsIndex = labels.indexOf(classifiedAs);
			final int trueIndex = labels.indexOf(fv.getLabel());
			confusionMatrix[trueIndex][classifiedAsIndex] += 1;
		}

		// print result
		double avgHitRate = 0;
		String s = "";
		for (int i = 0; i < confusionMatrix.length; i++) {
			double trueHits = confusionMatrix[i][i] * 1d
					/ Arrays2.sum(confusionMatrix[i]);
			avgHitRate += trueHits / confusionMatrix.length;

			s += labels.get(i) + ";";
			s += Arrays2.join(confusionMatrix[i], ";");
			s += String.format(";%f", trueHits);
			s += "\n";
		}
		s += ";" + Collections2.joinToString(labels, ";");
		s += String.format(";%f", avgHitRate);
		log.info("-----------\n" + s);
	}

	private HashMap<String, Double> getFinalWeights(
			HashMap<String, List<Double>> probabilityMap, int numClusters) {
		HashMap<String, Double> map = new HashMap<String, Double>();
		for (Entry<String, List<Double>> entry : probabilityMap.entrySet()) {
			map.put(entry.getKey(), Collections2.sum(entry.getValue())
					/ numClusters);
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

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Erstellt ein Kuchendiagramm";
	}

	@Override
	public String getVersion() {
		return "0.1 ALPHA";
	}

	@Override
	public Class<?> getDataClass() {
		return Data.class;
	}

}
