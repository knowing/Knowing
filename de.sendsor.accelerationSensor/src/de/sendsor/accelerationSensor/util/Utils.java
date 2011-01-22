package de.sendsor.accelerationSensor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.sendsor.accelerationSensor.algorithm.Category;

/**
 * Based on <i>Franz Graf's</i> Util-Class
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public class Utils {

	private static final Logger log = Logger.getLogger(Utils.class.getName());



	public static List<LabeledDoubleFeature> readRawFeaturesFromData(RawData data) {
		Assert.isTrue(data.dimension() == 3, "Dimension should be 3 instead: " + data.dimension()); 
		final List<LabeledDoubleFeature> returns = new ArrayList<LabeledDoubleFeature>();
		if(data == null)
			return returns;		
		
		for (int i = 0; i < data.size(); i++) {
				double[] values = new double[] { data.getDimension(0)[i], data.getDimension(1)[i], data.getDimension(2)[i] };
				LabeledDoubleFeature feature = new LabeledDoubleFeature(values, getLabel(i));
				returns.add(feature);
		}
		return returns;

	}

	// TODO WRONG!
	private static String getLabel(int index) {
		Category[] categories = Category.values();
		int newIndex = index % categories.length;
		// return categories[newIndex].name();
		return "sitzen";
	}

}
