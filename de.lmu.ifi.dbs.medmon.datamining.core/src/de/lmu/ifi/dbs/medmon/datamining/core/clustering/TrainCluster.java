package de.lmu.ifi.dbs.medmon.datamining.core.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.DoubleCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.util.LabeledDoubleFeature;
import de.lmu.ifi.dbs.medmon.datamining.core.util.Utils;
import de.lmu.ifi.dbs.utilities.Arrays2;
import de.lmu.ifi.dbs.utilities.Math2;

public class TrainCluster {

	private static final Logger log = Logger.getLogger(TrainCluster.class.getName());

	static final int KMEANS_K = 1000;
	static final int KMEANS_MAX_ITERATION = 20;
	static final int KERNEL_SIZE = 7;
	static final int MIN_INSTANCES_PER_CLUSTER = 5;

	public List<DoubleCluster> cluster(List<List<String>> list, String label) throws UnableToComplyException {
		List<LabeledDoubleFeature> rawVectors = Utils.readRawFeaturesFromData(list, label);
		List<LabeledDoubleFeature> converted = raw2Features(rawVectors);

		log.info("KMeans clustering");

		// fill db and map doublevectors to my vectors
		HashMap<DoubleVector, LabeledDoubleFeature> vecMap = new HashMap<DoubleVector, LabeledDoubleFeature>();
		Database<DatabaseObject> db = new SequentialDatabase<DatabaseObject>();
		final DatabaseObjectMetadata assoc = new DatabaseObjectMetadata();
		for (LabeledDoubleFeature v : converted) {
			DoubleVector elkivec = new DoubleVector(v.getValues());
			vecMap.put(elkivec, v);
			db.insert(new Pair<DatabaseObject, DatabaseObjectMetadata>(elkivec,	assoc));
		}

		// init KMeans

		log.info("k=" + KMEANS_K + "; maxiterations=" + KMEANS_MAX_ITERATION);
		KMeans kmeans = new KMeans(EuclideanDistanceFunction.STATIC, KMEANS_K,
				KMEANS_MAX_ITERATION);

		// do clustering
		List<DoubleCluster> doubleClusters = new ArrayList<DoubleCluster>();
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

			DoubleCluster newCluster = buildCluster(centroid, clusterVectors);
			if (newCluster.getNumChildren() > MIN_INSTANCES_PER_CLUSTER) {
				doubleClusters.add(newCluster);
			}
		}

		log.info("Found " + doubleClusters.size() + " clusters with > "
				+ MIN_INSTANCES_PER_CLUSTER + " members");
		return doubleClusters;
	}

	private List<LabeledDoubleFeature> raw2Features(
			List<LabeledDoubleFeature> raw) {
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

	private DoubleCluster buildCluster(double[] centroid,
			List<LabeledDoubleFeature> clusterVectors) {
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
		String topLabel = "none";
		Double topProbability = 0d;
		for (Entry<String, Double> entry : labels.entrySet()) {
			if (entry.getValue() > topProbability) {
				topLabel = entry.getKey();
				topProbability = entry.getValue();
			}
		}

		return new DoubleCluster(topLabel, centroid, clusterCount);
	}

}
