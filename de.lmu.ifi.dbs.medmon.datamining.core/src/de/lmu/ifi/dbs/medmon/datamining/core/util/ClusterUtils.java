package de.lmu.ifi.dbs.medmon.datamining.core.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.medmon.datamining.core.csv.CSVFileReader;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.sensor.core.util.FrameworkUtil;
import de.lmu.ifi.dbs.utilities.Arrays2;
import de.lmu.ifi.dbs.utilities.Math2;

/**
 * Based on <i>Franz Graf's</i> Util-Class
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public class ClusterUtils {

	private static final Logger log = Logger.getLogger(ClusterUtils.class.getName());
	
	public static List<LabeledDoubleFeature> readRawFeaturesFromData(File file, String label) throws IOException {
		final List<LabeledDoubleFeature> returns = new ArrayList<LabeledDoubleFeature>();
		System.out.println("File: " + file.getAbsolutePath() + "; Label: " + label);

		log.info("Converting CSV to DoubleLabeledFeature");
        CSVFileReader in = new CSVFileReader(file, ',');
        List<String> fields = in.readFields();
        while (fields != null) {
            fields.remove(0); // kill date value
            LabeledDoubleFeature v = new LabeledDoubleFeature(fields, label);
            returns.add(v);
            fields = in.readFields();
        }
        in.close();
		
        return returns;
	}
	
	public static List<LabeledDoubleFeature> raw2Features(List<LabeledDoubleFeature> raw) {
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

	
	public static IDataProcessor parseProcessingUnit(DataProcessingUnit dpu) {
		List<DataProcessor> processors = dpu.getProcessors();
		for (DataProcessor dataProcessor : processors) {
			String id = dataProcessor.getId();
			IDataProcessor processor = FrameworkUtil.findDataProcessor(id);
		}
		
		return null;
	}
	
}
