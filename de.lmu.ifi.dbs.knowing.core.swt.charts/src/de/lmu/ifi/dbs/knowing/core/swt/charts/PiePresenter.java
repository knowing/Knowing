package de.lmu.ifi.dbs.knowing.core.swt.charts;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * <p>
 * The PiePresenter can handle three types of instances:
 * <li>nominal class attribute only</li>
 * <li>nominal class attribute + probability</li>
 * <li>nominal class attribute (top probability) + probability per class label</li>
 * </p>
 * <p>
 * There should only be one nominal attribute, because if the dataset sets no<br>
 * class attribute, the PiePresenter will guess, which means he choose the first
 * nominal<br>
 * attribute he can find and treat (but not set!) this as the class attribute
 * </p>
 * 
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 30.03.2011
 * 
 */
public class PiePresenter extends AbstractChartPresenter {

	/** label -> probability || label -> count */
	private final Map<String, Double> model = new HashMap<String, Double>();
	private final Map<String, Integer> labelCount = new HashMap<String, Integer>();

	private int num_instances = 0;

	private int mode = -1;

	private static final int MODE_LABEL_ONLY = 0;
	private static final int MODE_LABEL_PROBABILITY = 1;
	private static final int MODE_LABEL_DISTRIBUTION = 2;

	public PiePresenter() {
		super("Pie Presenter");
	}

	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */

	@Override
	protected void createContent(Instances dataset) {
		int classIndex = -1;
		if (model.isEmpty())
			classIndex = initModel(dataset);

		if (dataset.numAttributes() == 1)
			updateModelClassOnly(dataset);
		else if (dataset.numAttributes() == 2)
			updateModelClassProbability(dataset, classIndex);
		else
			;

		updateDataset();
		updateChart();

	}

	/**
	 * Just sums the count of class labels
	 * 
	 * @param dataset
	 */
	private void updateModelClassOnly(Instances dataset) {
//		if (mode != -1 || MODE_LABEL_ONLY != mode) {
//			initModel(dataset);
//			mode = MODE_LABEL_ONLY;
//		}

		ArrayList<Instance> instances = Collections.list(dataset.enumerateInstances());
		Attribute attribute = dataset.attribute(0);
		for (Instance instance : instances) {
			num_instances++;
			int index = (int) instance.value(0);
			String label = attribute.value(index);
			double count = model.get(label);
			model.put(label, count++);
		}

	}

	/**
	 * <p>This method assumes that either
	 * <li>each label appears only one time</li>
	 * <li>
	 * @param dataset
	 * @param classIndex
	 */
	private void updateModelClassProbability(Instances dataset, int classIndex) {
//		if (mode != -1 || MODE_LABEL_PROBABILITY != mode) {
//			classIndex = initModel(dataset);
//			mode = MODE_LABEL_PROBABILITY;
//		}

		int valIndex = findValueIndex(dataset);
		ArrayList<Instance> instances = Collections.list(dataset.enumerateInstances());
		for (Instance instance : instances) {
			String label = instance.stringValue(classIndex);
			double value = instance.value(valIndex);
			double oldValue = model.get(label);
			// assumes that probabilities always sum up to 100%
			double newValue = (value + oldValue);
			model.put(label, newValue);
			
			//Increase the label account
			Integer count = labelCount.get(label);
			labelCount.put(label, count++);
		}
		
		//Find maximum count
		int maxCount = 1;
		for (Integer each : labelCount.values()) {
			if(each > maxCount)
				maxCount = each;
		}
		//Reset labelCount
		for (String key : labelCount.keySet())
			labelCount.put(key, 0);
		
		//Normalize values
		for (String key : model.keySet()) {
			double value = model.get(key);
			double normalized = value / maxCount;
			model.put(key, normalized);
		}

	}

	private void updateDataset() {
		DefaultPieDataset pieset = ((DefaultPieDataset) dataset);
		pieset.clear();
		for (String key : model.keySet()) {
			Double propability = model.get(key);
			pieset.setValue(key, propability);
		}
	}

	/**
	 * 
	 * @param dataset
	 * @return the first numeric index found, if non exists a negative value
	 */
	private int findValueIndex(Instances dataset) {
		ArrayList<Attribute> attributes = Collections.list(dataset.enumerateAttributes());
		for (Attribute attribute : attributes)
			if (attribute.isNumeric())
				return attribute.index();
		return -1;
	}

	/**
	 * @param dataset
	 * @return the first found nominal attribute index
	 */
	private int guessClassIndex(Instances dataset) {
		ArrayList<Attribute> attributes = Collections.list(dataset.enumerateAttributes());
		for (Attribute attribute : attributes) {
			if (attribute.isNominal())
				return attribute.index();
		}
		return -1;
	}

	/**
	 * <p>
	 * Sets the distribution for all lables to zero<br>
	 * to take effect call {@link #updateDataset()} and {@link #updateChart()}
	 * </p>
	 * 
	 * @param classAttribute - must be nominal
	 */
	private int initModel(Instances dataset) {
		int classIndex = dataset.classIndex();
		if (classIndex < 0 && model.isEmpty())
			classIndex = guessClassIndex(dataset);
		Attribute classAttribute = dataset.attribute(classIndex);
		if (!classAttribute.isNominal())
			return classIndex;
		
		ArrayList<String> labels = Collections.list(classAttribute.enumerateValues());
		for (String label : labels) {
			model.put(label, 0.0);
			labelCount.put(label, 0);
		}

		num_instances = 0;
		return classIndex;
	}

	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */

	@Override
	protected JFreeChart createChart(Dataset dataset) {
		JFreeChart chart = ChartFactory.createPieChart3D("Pie Chart", (PieDataset) dataset, true, false, false);
		// JFreeChart chart = new JFreeChart("Pie Chart", plot);
		return chart;
	}

	@Override
	protected Dataset createDataset() {
		return new DefaultPieDataset();
	}

	@Override
	protected void configurePlot(Plot plot) {
		PiePlot3D plot3D = (PiePlot3D) plot;
		plot3D.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot3D.setNoDataMessage("No data available");
		plot3D.setCircular(false);
		plot3D.setLabelGap(0.02);

		plot3D.setStartAngle(270);
		plot3D.setDirection(Rotation.ANTICLOCKWISE);
		plot3D.setForegroundAlpha(0.60f);
		plot3D.setBackgroundPaint(Color.white);
	}

}
