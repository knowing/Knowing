package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;

public class TimePeriodValuesAnalyzedData extends AbstractXYAnalyzedData {

	private final Map<Comparable, TimePeriodValues> seriesMap = new HashMap<Comparable, TimePeriodValues>();

	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */

	public void addSeries(TimePeriodValues series) {
		TimePeriodValuesCollection dataset = (TimePeriodValuesCollection) this.dataset;
		dataset.addSeries(series);
		seriesMap.put(series.getKey(), series);
	}

	public void add(String key, TimePeriod period, double value) {
		TimePeriodValues series = seriesMap.get(key);
		if (series == null) {
			series = new TimePeriodValues(key);
			addSeries(series);
		}
		series.add(period, value);
	}

	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */

	@Override
	protected XYDataset createDataset() {
		return new TimePeriodValuesCollection();
	}

	@Override
	protected JFreeChart createChart(Dataset dataset) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Time Series", "Date", "Activity", (XYDataset) dataset,
				true, true, false);
		return chart;
	}

	@Override
	protected void configurePlot(Plot plot) {
		XYPlot plotXY = (XYPlot) plot;
		plotXY.setBackgroundPaint(Color.white);
		plotXY.setDomainGridlinePaint(Color.lightGray);
		plotXY.setRangeGridlinePaint(Color.lightGray);
	}

}
