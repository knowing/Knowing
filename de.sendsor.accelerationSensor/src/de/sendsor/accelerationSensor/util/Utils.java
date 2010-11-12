package de.sendsor.accelerationSensor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.sendsor.accelerationSensor.algorithm.Category;

/**
 * Based on <i>Franz Graf's</i> Util-Class
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public class Utils {

	private static final Logger log = Logger.getLogger(Utils.class.getName());
	
	public static List<LabeledDoubleFeature> readRawFeaturesFromData(Data[] data) {
		final List<LabeledDoubleFeature> returns = new ArrayList<LabeledDoubleFeature>();
		if(data == null)
			return returns;		
		
		int index = 0;
		for (Data each : data) {
			double[] values = new double[] { each.getX(), each.getY(), each.getZ() };
			LabeledDoubleFeature feature = new LabeledDoubleFeature(values, getLabel(index++));
			returns.add(feature);
		}
		return returns;
	}
	
	//TODO WRONG!
	private static String getLabel(int index) {
		Category[] categories = Category.values();
		int newIndex = index % categories.length;
		//return categories[newIndex].name();
		return "sitzen";
	}

}
