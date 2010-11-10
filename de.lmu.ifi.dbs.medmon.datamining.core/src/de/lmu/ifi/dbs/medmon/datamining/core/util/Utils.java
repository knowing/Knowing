package de.lmu.ifi.dbs.medmon.datamining.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.*;
import de.lmu.ifi.dbs.medmon.sensor.core.util.FrameworkUtil;

/**
 * Based on <i>Franz Graf's</i> Util-Class
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public class Utils {

	private static final Logger log = Logger.getLogger(Utils.class.getName());
	
	public static List<LabeledDoubleFeature> readRawFeaturesFromData(List<List<String>> list, String label) {
		final List<LabeledDoubleFeature> returns = new ArrayList<LabeledDoubleFeature>();
		if(list == null || list.isEmpty())
			return returns;		
		
		for (List<String> each : list) {
			//kill date value
			each.remove(0);
			LabeledDoubleFeature feature = new LabeledDoubleFeature(each, label);
			returns.add(feature);
		}
		return returns;
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
