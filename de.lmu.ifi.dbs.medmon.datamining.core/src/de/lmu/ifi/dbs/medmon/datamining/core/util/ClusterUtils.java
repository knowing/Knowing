package de.lmu.ifi.dbs.medmon.datamining.core.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.csv.io.CSVFileReader;
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

	public static List<LabeledDoubleFeature> readRawFeaturesFromData(RawData data) {
		final List<LabeledDoubleFeature> returns = new ArrayList<LabeledDoubleFeature>();
		log.info("Converting RawData to DoubleLabeledFeature");
		int length = data.getDimension(0).length;
		for (int i = 0; i < length; i++) {
			double[] values = new double[data.dimension()];
			for (int j = 0; j < data.dimension(); j++) {
				values[j] = data.getDimension(j)[i];
			}
			returns.add(new LabeledDoubleFeature(values, data.getLabel()));
		}

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
			compact.add(new LabeledDoubleFeature(newValues, raw.get(0).getLabel()));
		}

		return compact;
	}

	/**
	 * The CSVFileReader must be initialized with a CSVDescriptor Time
	 * inefficient / Space efficient
	 * 
	 * @param reader
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static RawData convertFromCSV(CSVFileReader reader) throws NumberFormatException, IOException, ParseException {
		Map<Integer, Class> fields = reader.getDescriptor().getFields();
		List<Integer> positions = new ArrayList<Integer>();
		for (Integer position : fields.keySet()) {
			Class clazz = fields.get(position);
			if (clazz == Double.class)
				positions.add(position); // This is a double value
		}

		int dimension = 0;
		RawData rawData = new RawData(positions.size());
		for (Integer position : positions) {
			List<Double> list = new LinkedList<Double>();
			Map<Integer, Object> line = reader.readFieldsToMap();
			while (line != null) {
				list.add((Double) line.get(position));
				line = reader.readFieldsToMap();
			}
			reader = reader.recreate(); // start from beginning
			rawData.setDimension(dimension++, toArray(list));
		}

		return rawData;
	}

	private static double[] toArray(List<Double> list) {
		double[] returns = new double[list.size()];
		int i = 0;
		for (Double d : list)
			returns[i++] = d;
		return returns;
	}
	


}
