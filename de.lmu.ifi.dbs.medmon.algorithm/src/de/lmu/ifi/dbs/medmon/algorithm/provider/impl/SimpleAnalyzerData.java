package de.lmu.ifi.dbs.medmon.algorithm.provider.impl;

import java.awt.*;
import java.text.DecimalFormat;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.axis.*;
import org.jfree.data.category.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

import de.lmu.ifi.dbs.medmon.algorithm.extension.IAnalyzedData;

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
