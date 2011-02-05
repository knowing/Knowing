package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;

public class HistogramAnalyzedData extends AbstractXYAnalyzedData {


	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */

	public void addSeries(String name, double[] values, int bins) {
		addSeries(name, values, bins, 0.0, 10.0);
	}

	public void addSeries(String name, double[] values, int bins, double min, double max) {
		((HistogramDataset)dataset).addSeries(name, values, bins, min, max);
	}

	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */

	@Override
	protected XYDataset createDataset() {
		return new HistogramDataset();
	}

	@Override
	protected JFreeChart createChart(Dataset dataset) {
		JFreeChart chart = ChartFactory.createHistogram("Histogram Demo 1", null, null, (HistogramDataset) dataset,
				PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	@Override
	protected void configurePlot(Plot plot) {
		XYPlot plotXY = (XYPlot) plot;
		
		XYBarRenderer renderer = (XYBarRenderer) plotXY.getRenderer();
		renderer.setDrawBarOutline(false);
		// flat bars look best...
		renderer.setBarPainter(new StandardXYBarPainter());
		renderer.setShadowVisible(false);
			
		
		plotXY.setForegroundAlpha(0.85f);
		plotXY.setOrientation(PlotOrientation.VERTICAL);

	}

}
