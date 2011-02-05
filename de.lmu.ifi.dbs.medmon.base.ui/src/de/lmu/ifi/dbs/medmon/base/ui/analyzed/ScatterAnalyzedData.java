package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterAnalyzedData extends AbstractXYAnalyzedData {

	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */

	public void addSeries(XYSeries series) {
		XYSeriesCollection dataset = (XYSeriesCollection) this.dataset;
		dataset.addSeries(series);
	}

	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */

	@Override
	protected JFreeChart createChart(Dataset dataset) {
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
		NumberAxis domainAxis = (NumberAxis) plotXY.getDomainAxis();
		domainAxis.setAutoRangeIncludesZero(false);

	}

}
