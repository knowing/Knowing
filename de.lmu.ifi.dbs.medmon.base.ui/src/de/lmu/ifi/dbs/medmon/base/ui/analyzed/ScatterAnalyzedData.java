package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterAnalyzedData extends AbstractXYAnalyzedData {

	public static final int SHAPED_DOTS = 0;
	public static final int PLAIN_DOTS = 1;

	private final int style;

	public ScatterAnalyzedData() {
		this(SHAPED_DOTS);
	}

	public ScatterAnalyzedData(int style) {
		this.style = style;
	}

	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */

	public void addSeries(XYSeries series) {
		XYSeriesCollection dataset = (XYSeriesCollection) this.dataset;
		dataset.addSeries(series);
	}
	
	public void addSeries(String key) {
		addSeries(new XYSeries(key));
	}
	
	public void add(String key, double x, double y) {
		XYSeriesCollection dataset = (XYSeriesCollection) this.dataset;
		XYSeries series = null;
		try {
			series = dataset.getSeries(key);
		} catch (UnknownKeyException e) {
			series = new XYSeries(key);
			dataset.addSeries(series);
		} finally {
			series.add(x, y);
		}
		
			
	}

	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */

	@Override
	protected JFreeChart createChart(Dataset dataset) {
		//if(style == SHAPED_DOTS) {
			JFreeChart chart = ChartFactory.createScatterPlot("Scatter", "x", "y", (XYDataset) dataset,
					PlotOrientation.VERTICAL, true, false, false);
			return chart;


	}

	@Override
	protected XYDataset createDataset() {
		return new XYSeriesCollection();
	}

	@Override
	protected void configurePlot(Plot plot) {
		XYPlot plotXY = (XYPlot) plot;
		plotXY.setBackgroundPaint(Color.white);
		plotXY.setDomainGridlinePaint(Color.lightGray);
		plotXY.setRangeGridlinePaint(Color.lightGray);
		
		NumberAxis domainAxis = (NumberAxis) plotXY.getDomainAxis();
		domainAxis.setAutoRangeIncludesZero(false);
		if(style == PLAIN_DOTS) {
			XYDotRenderer renderer = new XYDotRenderer();
			renderer.setDotHeight(2);
			renderer.setDotWidth(2);
			plotXY.setRenderer(renderer);
		}
	}

}
