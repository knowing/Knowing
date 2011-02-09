package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

public class BubbleAnalyzedData extends AbstractXYAnalyzedData {

	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */

	public void addSeries(Comparable key, double[][] series) {
		DefaultXYZDataset dataset = (DefaultXYZDataset) this.dataset;
		dataset.addSeries(key, series);
	}

	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */

	@Override
	protected JFreeChart createChart(Dataset dataset) {
		 JFreeChart chart = ChartFactory.createBubbleChart(
		 "Bubble Chart Demo 1", "X", "Y", (XYZDataset) dataset,
		 PlotOrientation.HORIZONTAL, true, true, false);
		
		ChartUtilities.applyCurrentTheme(chart);
		return chart;
	}

	@Override
	protected XYDataset createDataset() {
		return new DefaultXYZDataset();
	}

	@Override
	protected void configurePlot(Plot plot) {
		XYPlot plotXY = (XYPlot) plot;
		
		XYItemRenderer renderer = plotXY.getRenderer();
		renderer.setSeriesPaint(0, Color.blue);

		// increase the margins to account for the fact that the auto-range
		// doesn't take into account the bubble size...
		NumberAxis domainAxis = (NumberAxis) plotXY.getDomainAxis();
		domainAxis.setLowerMargin(0.15);
		domainAxis.setUpperMargin(0.15);
		//plot.setDomainAxis(domainAxis);
		NumberAxis rangeAxis = (NumberAxis) plotXY.getRangeAxis();
		rangeAxis.setLowerMargin(0.15);
		rangeAxis.setUpperMargin(0.15);
		//plot.setRangeAxis(rangeAxis);
		
		
		plotXY.setForegroundAlpha(0.65f);
		plotXY.setOrientation(PlotOrientation.HORIZONTAL);
		plotXY.setBackgroundPaint(Color.white);
		plotXY.setDomainGridlinePaint(Color.lightGray);
		plotXY.setRangeGridlinePaint(Color.lightGray);

	}

}
