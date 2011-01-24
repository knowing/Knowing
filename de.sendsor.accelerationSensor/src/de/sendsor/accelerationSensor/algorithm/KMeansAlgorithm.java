package de.sendsor.accelerationSensor.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import de.lmu.ifi.dbs.medmon.datamining.core.analyzed.ClusterAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.analyzed.TableAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.DoubleCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.clustering.TrainCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.NumericParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.AbstractAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.util.ClusterUtils;
import de.lmu.ifi.dbs.medmon.datamining.core.util.LabeledDoubleFeature;
import de.lmu.ifi.dbs.utilities.Arrays2;
import de.lmu.ifi.dbs.utilities.Math2;

public class KMeansAlgorithm extends AbstractAlgorithm {

	private static final Logger log = Logger.getLogger(TrainCluster.class.getName());

	private static final int KMEANS_K = 1000;
	private static final int KMEANS_MAX_ITERATION = 20;
	private static final int KERNEL_SIZE = 7;
	private static final int MIN_INSTANCES_PER_CLUSTER = 5;
	
	//Keys
	private static final String KMEANS_K_KEY = "KMEANS K";
	private static final String KMEANS_MAX_ITERATION_KEY = "KMEANS MAX ITERATION";
	private static final String KERNEL_SIZE_KEY = "KERNEL SIZE";
	private static final String MIN_INSTANCES_PER_CLUSTER_KEY = "MIN INSTANCES PER CLUSTER";
	
	private ClusterAnalyzedData clusterAnalyzedData;
	private TableAnalyzedData tableAnalyzedData;

	public KMeansAlgorithm() {
		super("KMeans Algorithm", INDEFINITE_DIMENSION, INDEFINITE_DIMENSION);
		init();
	}
	
	private void init() {
		parameters.put(KMEANS_K_KEY, new NumericParameter(KMEANS_K_KEY, 0, 5000, KMEANS_K));
		parameters.put(KMEANS_MAX_ITERATION_KEY, new NumericParameter(KMEANS_MAX_ITERATION_KEY, 0, 200, KMEANS_MAX_ITERATION));
		parameters.put(KERNEL_SIZE_KEY, new NumericParameter(KERNEL_SIZE_KEY, 0, 50, KERNEL_SIZE));
		parameters.put(MIN_INSTANCES_PER_CLUSTER_KEY, new NumericParameter(MIN_INSTANCES_PER_CLUSTER_KEY, 0, 50, MIN_INSTANCES_PER_CLUSTER));
	}

	@Override
	public Map<String, IAnalyzedData> process(RawData data) {
		clusterAnalyzedData = new ClusterAnalyzedData();
		tableAnalyzedData = TableAnalyzedData.getInstance(new String[] { "Label", "Centroid" });
		analyzedData.put(CLUSTER_DATA, clusterAnalyzedData);
		analyzedData.put(DEFAULT_DATA, tableAnalyzedData);
		analyzedData.put(TABLE_DATA, tableAnalyzedData);
		try {
			List<DoubleCluster> cluster = cluster(data);
			clusterAnalyzedData.setClusterlist(cluster);
			for (DoubleCluster doubleCluster : cluster) {
				String centroid = Arrays2.join(doubleCluster.getCentroidArray(), " ; ");
				tableAnalyzedData.addRow(doubleCluster.getLabel(), centroid);
			}
			
		} catch (UnableToComplyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return analyzedData;
	}

	@Override
	public Map<String, IAnalyzedData> process(RawData data, Map<String, IAnalyzedData> analyzedData) {	
		this.clusterAnalyzedData = (ClusterAnalyzedData) analyzedData.get(CLUSTER_DATA);
		this.tableAnalyzedData = (TableAnalyzedData) analyzedData.get(TABLE_DATA);
		try {
			List<DoubleCluster> cluster = cluster(data);
			for (DoubleCluster doubleCluster : cluster) {
				clusterAnalyzedData.addCluster(doubleCluster);
				String centroid = Arrays2.join(doubleCluster.getCentroidArray(), ";");
				tableAnalyzedData.addRow(doubleCluster.getLabel(), centroid);
			}
		} catch (UnableToComplyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return analyzedData;
	}

	@Override
	public boolean isTimeSensitiv() {
		return false;
	}

	@Override
	public String[] analyzedDataKeys() {
		return new String[] { CLUSTER_DATA };
	}

	public List<DoubleCluster> cluster(RawData data) throws UnableToComplyException, IOException {
		List<LabeledDoubleFeature> features = createFeatures(data);
		log.info("features: " + features.size());

		log.info("KMeans clustering");

		// fill db and map doublevectors to my vectors
		HashMap<DoubleVector, LabeledDoubleFeature> vecMap = new HashMap<DoubleVector, LabeledDoubleFeature>();
		Database<DatabaseObject> db = new SequentialDatabase<DatabaseObject>();
		final DatabaseObjectMetadata assoc = new DatabaseObjectMetadata();
		for (LabeledDoubleFeature v : features) {
			DoubleVector elkivec = new DoubleVector(v.getValues());
			vecMap.put(elkivec, v);
			db.insert(new Pair<DatabaseObject, DatabaseObjectMetadata>(elkivec, assoc));
		}

		// init KMeans
		int kmeans_k = (Integer) parameters.get(KMEANS_K_KEY).getValue();
		int kmeans_max_iteration = (Integer) parameters.get(KMEANS_MAX_ITERATION_KEY).getValue();
		int kernel_size = (Integer) parameters.get(KERNEL_SIZE_KEY).getValue();
		int min_instances = (Integer) parameters.get(MIN_INSTANCES_PER_CLUSTER_KEY).getValue();

		log.info("k=" + kmeans_k + "; maxiterations=" + kmeans_max_iteration);
		KMeans kmeans = new KMeans(EuclideanDistanceFunction.STATIC, kmeans_k, kmeans_max_iteration);

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
			if (newCluster.getNumChildren() > min_instances) {
				doubleClusters.add(newCluster);
			}
		}

		log.info("Found " + doubleClusters.size() + " clusters with > " + min_instances + " members");
		return doubleClusters;
	}

	private List<LabeledDoubleFeature> createFeatures(File[] files, String[] lables) throws IOException {
		List<LabeledDoubleFeature> features = new ArrayList<LabeledDoubleFeature>();
		for (int i = 0; i < lables.length; i++) {
			String label = lables[i];
			File file = files[i];
			List<LabeledDoubleFeature> rawVectors = ClusterUtils.readRawFeaturesFromData(file, label);
			List<LabeledDoubleFeature> converted = raw2Features(rawVectors);
			features.addAll(converted);
		}
		return features;
	}

	private List<LabeledDoubleFeature> createFeatures(RawData data) {
		List<LabeledDoubleFeature> features = new ArrayList<LabeledDoubleFeature>();
		List<LabeledDoubleFeature> rawVectors = ClusterUtils.readRawFeaturesFromData(data);
		List<LabeledDoubleFeature> converted = raw2Features(rawVectors);
		features.addAll(converted);
		return features;
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
			compact.add(new LabeledDoubleFeature(newValues, raw.get(0).getLabel()));
		}

		return compact;
	}

	private DoubleCluster buildCluster(double[] centroid, List<LabeledDoubleFeature> clusterVectors) {
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
