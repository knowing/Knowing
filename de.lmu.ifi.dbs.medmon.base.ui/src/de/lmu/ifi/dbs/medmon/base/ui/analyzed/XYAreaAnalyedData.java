package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class XYAreaAnalyedData extends AbstractXYAnalyzedData {

	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */

	public void addSeries(TimeSeries series) {
		((TimeSeriesCollection) dataset).addSeries(series);
	}

	/**
	 * This method uses the initial given TimePeriod given in
	 * addSeries(TimeSeries series).
	 * 
	 * @param key
	 * @param value
	 */
	public void addNextPeriod(Comparable key, double value) {
		TimeSeries series = ((TimeSeriesCollection)dataset).getSeries(key);
		RegularTimePeriod nextTimePeriod = series.getNextTimePeriod();
		series.add(nextTimePeriod, value);
	}
	
	public void addPeriod(Comparable key, double value, RegularTimePeriod period) {
		TimeSeries series = ((TimeSeriesCollection)dataset).getSeries(key);
		series.addOrUpdate(period, value);
	}

	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */

	@Override
	protected XYDataset createDataset() {
		return new TimeSeriesCollection();
	}

	@Override
	protected JFreeChart createChart(Dataset dataset) {
		JFreeChart chart = ChartFactory.createXYAreaChart("XY Area Chart Demo 2", "Time", "Value", (XYDataset) dataset,
				PlotOrientation.VERTICAL, true, // legend
				true, // tool tips
				false // URLs
				);
		return chart;
	}

	@Override
	protected void configurePlot(Plot plot) {
		XYPlot plotXY = (XYPlot) plot;
		ValueAxis domainAxis = new DateAxis("Time");
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		plotXY.setDomainAxis(domainAxis);
		plotXY.setForegroundAlpha(0.5f);
		plotXY.setBackgroundPaint(Color.white);
		plotXY.setDomainGridlinePaint(Color.lightGray);
		plotXY.setRangeGridlinePaint(Color.lightGray);

		XYItemRenderer renderer = plotXY.getRenderer();
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("d-MMM-yyyy"),
				new DecimalFormat("#,##0.00")));
	}
}
