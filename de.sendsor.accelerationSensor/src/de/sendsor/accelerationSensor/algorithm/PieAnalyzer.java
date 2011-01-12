package de.sendsor.accelerationSensor.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.medmon.datamining.core.analyzed.TableAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.DoubleCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.ClusterParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.AbstractAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.utilities.Arrays2;
import de.lmu.ifi.dbs.utilities.Math2;
import de.lmu.ifi.dbs.utilities.PriorityQueue;
import de.lmu.ifi.dbs.utilities.distances.EuclideanSquared;
import de.sendsor.accelerationSensor.util.LabeledDoubleFeature;
import de.sendsor.accelerationSensor.util.Utils;

/**
 * Analyze the data via a simple clustering algorithm.
 * 
 * @author Nepomuk Seiler
 * @version 0.5
 */
public class PieAnalyzer extends AbstractAlgorithm {

	public static final String NAME = "Clustervergleich";
	
	private static final Logger log = Logger.getLogger(PieAnalyzer.class.getName());

	private static final String PIE_CHART = "Pie Chart";

	public PieAnalyzer() {
		super(NAME, 3);
		init();
	}

	private void init() {
		ClusterParameter parameter = new ClusterParameter("Cluster");
		parameters.put(parameter.getName(), parameter);
		
		analyzedData.put(PIE_CHART, null);
		
		description = "Erstellt ein Kuchendiagramm";
		version = "0.4 ALPHA";
	}

	@Override
	public Map<String, IAnalyzedData> process(RawData data) {

		List<LabeledDoubleFeature> features = new ArrayList<LabeledDoubleFeature>();
		List<LabeledDoubleFeature> rawVectors = Utils.readRawFeaturesFromData(data);
		List<LabeledDoubleFeature> converted = raw2Features(rawVectors);
		log.info("read " + rawVectors.size() + ", compactified to " + converted.size());
		features.addAll(converted);

		log.info("features: " + features.size());
		// writeToCSV(features, new File("./sendsor.csv"));

		// test
		List<DoubleCluster> cluster = getCluster();
		test(cluster, features);
		return analyzedData;
	}

	@Override
	public Map<String, IAnalyzedData> process(RawData data, Map<String, IAnalyzedData> analyzedData) {
		return null;
	}

	@Override
	public String[] analyzedDataKeys() {
		return new String[] { TABLE_DATA, PIE_CHART };
	}

	private List<DoubleCluster> getCluster() {
		for (IProcessorParameter<?> parameter : parameters.values()) {
			if (parameter instanceof ClusterParameter) {
				ClusterParameter p = (ClusterParameter) parameter;
				ClusterUnit value = p.getValue();
				return value.getClusterlist();
			}
		}
		return Collections.EMPTY_LIST;
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

	private void test(List<DoubleCluster> clusters, List<LabeledDoubleFeature> test) {
		final EuclideanSquared dist = new EuclideanSquared();
		List<String> labels = new ArrayList<String>();
		for (DoubleCluster c : clusters) {
			if (!labels.contains(c.getLabel())) {
				System.out.println("Label added: " + c.getLabel());
				labels.add(c.getLabel());
			}
		}

		// classify
		int[] confusionMatrix = new int[labels.size()];
		for (LabeledDoubleFeature fv : test) {
			PriorityQueue<String> pq = new PriorityQueue<String>(true);
			for (DoubleCluster c : clusters) {
				double d = dist.dist(c.getCentroidArray(), fv.getValues());
				pq.add(d, c.getLabel());
			}

			final int classifiedAsIndex = labels.indexOf(pq.getFirst());
			confusionMatrix[classifiedAsIndex] += 1;
		}
		
		PieAnalyzerData pieData = new PieAnalyzerData();
		TableAnalyzedData tableData = TableAnalyzedData.getInstance(new String[] { "Cluster", "Hit" });
		StringBuffer sb = new StringBuffer();
		double sum = Arrays2.sum(confusionMatrix);
		for (int i = 0; i < confusionMatrix.length; i++) {
			sb.append(labels.get(i));
			sb.append(": ");
			sb.append(confusionMatrix[i]);
			sb.append("; ");
			sb.append("Percent: ");
			sb.append((confusionMatrix[i] / sum) * 100);
			sb.append("\n");
			double value = ((confusionMatrix[i] / sum) * 100);
			pieData.setValue(labels.get(i), value);
			long rValue = Math.round(value);
			tableData.addRow(labels.get(i), String.valueOf(rValue) + "%");
																						
		}
		log.info("-----------\n" + sb.toString());
		analyzedData.put(DEFAULT_DATA, tableData);
		analyzedData.put(TABLE_DATA, tableData);
		analyzedData.put(PIE_CHART, pieData);
	}

	@Override
	public boolean isTimeSensitiv() {
		return false;
	}


}
