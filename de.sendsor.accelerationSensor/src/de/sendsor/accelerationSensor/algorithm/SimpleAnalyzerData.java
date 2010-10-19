package de.sendsor.accelerationSensor.algorithm;

import java.awt.Color;
import java.awt.GradientPaint;
import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.IntervalBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import de.lmu.ifi.dbs.medmon.sensor.core.algorithm.IAnalyzedData;

public class SimpleAnalyzerData implements IAnalyzedData {

	@Override
	public void createContent(Composite parent) {
		CategoryDataset categorydataset = createDataset();
		JFreeChart chart = createChart(categorydataset);
		new ChartComposite(parent, SWT.NONE, chart, true);
	}


	private JFreeChart createChart(CategoryDataset dataset) {

		CategoryAxis domainAxis = new CategoryAxis("Category");
		NumberAxis rangeAxis = new NumberAxis("Percentage");
		rangeAxis.setNumberFormatOverride(new DecimalFormat("0.00%"));
		
		IntervalBarRenderer renderer = new IntervalBarRenderer();
		CategoryPlot plot = new CategoryPlot(dataset, domainAxis, rangeAxis,renderer);
		plot.setOrientation(PlotOrientation.HORIZONTAL);
		plot.setBackgroundPaint(Color.WHITE);
		

		final GradientPaint paint = new GradientPaint(0.0F, 0.0F,Color.lightGray, 0.0F, 0.0F, Color.lightGray);

		renderer.setSeriesPaint(0, paint);

		return new JFreeChart("Barchart", plot);
	}

	/**
	 * Returns a sample dataset.
	 * 
	 * @return The dataset.
	 */
	private IntervalCategoryDataset createDataset() {
		double[][] starts = new double[][] { { 0.0, 0.4, 0.1 } };
		double[][] ends = new double[][] { { 0.1, 0.6, 0.4 } };
		return new DefaultIntervalCategoryDataset(starts, ends);
	}

}
