package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;

public class IntervalBarAnalyzedData extends AbstractXYAnalyzedData {

	/* Category labels */
	private List<String> labels = new ArrayList<String>();

	/* Series map by label */
	private Map<String, XYIntervalSeries> series = new HashMap<String, XYIntervalSeries>();

	public IntervalBarAnalyzedData() {
		super();
	}

	public IntervalBarAnalyzedData(String[] labels) {
		for (String label : labels) {
			this.labels.add(label);
			XYIntervalSeries s = new XYIntervalSeries(label);
			series.put(label, s);
			((XYIntervalSeriesCollection) dataset).addSeries(s);
		}
	}

	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */

	public void addPeriod(RegularTimePeriod p0, RegularTimePeriod p1, String label) {
		XYIntervalSeries s = series.get(label);

		// Create new series
		if (s == null) {
			s = new XYIntervalSeries(label);
			labels.add(label);
			((XYIntervalSeriesCollection) dataset).addSeries(s);
		}
		// add period
		int index = labels.indexOf(label);
		s.add(index, index - 0.45, index + 0.45, p0.getFirstMillisecond(), p0.getFirstMillisecond(),
				p1.getLastMillisecond());
	}

	public void addPeriod(RegularTimePeriod p0, String category) {
		addPeriod(p0, p0, category);
	}

	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */

	@Override
	protected IntervalXYDataset createDataset() {
		return new XYIntervalSeriesCollection();
	}

	@Override
	protected JFreeChart createChart(Dataset dataset) {
		JFreeChart chart = ChartFactory.createXYBarChart("Interval Bar Chart",
                "Date", true, "Y", (IntervalXYDataset) dataset, PlotOrientation.HORIZONTAL,
                true, false, false);
		return chart;
	}

	/**
	 * The category names
	 * 
	 * @return
	 */
	public String[] getLabels() {
		return (String[]) labels.toArray(new String[labels.size()]);
	}

	@Override
	protected void configurePlot(Plot plot) {
		XYPlot plotXY = (XYPlot) plot;
		
		XYBarRenderer renderer = (XYBarRenderer) plotXY.getRenderer();
		renderer.setUseYInterval(true);
		
		SymbolAxis yAxis = new SymbolAxis("Kategorien", getLabels());
		yAxis.setGridBandsVisible(false);
		plotXY.setDomainAxis(yAxis);
		plotXY.setRangeAxis(new DateAxis("Datum"));

		plotXY.setBackgroundPaint(Color.white);
		plotXY.setDomainGridlinePaint(Color.lightGray);
		plotXY.setRangeGridlinePaint(Color.lightGray);
		plotXY.setOrientation(PlotOrientation.HORIZONTAL);
	}


}
