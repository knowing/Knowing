package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Minute;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class XYAnalyzedData extends AbstractXYAnalyzedData {

	@Override
	protected XYDataset createDataset() {
        XYSeries series = new XYSeries("Series 1");
        series.add(2.0, 56.27);
        series.add(3.0, 41.32);
        series.add(4.0, 31.45);
        series.add(5.0, 30.05);
        series.add(6.0, 24.69);
        series.add(7.0, 19.78);
        series.add(8.0, 20.94);
        series.add(9.0, 16.73);
        series.add(10.0, 14.21);
        series.add(11.0, 12.44);
        XYSeriesCollection result = new XYSeriesCollection(series);
        XYSeries series2 = new XYSeries("Series 2");
        series2.add(11.0, 56.27);
        series2.add(10.0, 41.32);
        series2.add(9.0, 31.45);
        series2.add(8.0, 30.05);
        series2.add(7.0, 24.69);
        series2.add(6.0, 19.78);
        series2.add(5.0, 20.94);
        series2.add(4.0, 16.73);
        series2.add(3.0, 14.21);
        series2.add(2.0, 12.44);
        result.addSeries(series2);
        return result;
	}

	@Override
	protected JFreeChart createChart(Dataset dataset) {
		//JFreeChart chart = ChartFactory.createTimeSeriesChart("Crosshair Demo 2", "Time of Day", "Value", (XYDataset) dataset, true,
		//		true, false);
		
		JFreeChart chart = ChartFactory.createXYLineChart("XY Line", "x", "y", (XYDataset) dataset, PlotOrientation.VERTICAL, true, false, false);
		//chart.addChangeListener(this);
		//chart.addProgressListener(this);
		//ChartUtilities.applyCurrentTheme(chart);
		return chart;
	}

	@Override
	protected void configurePlot(Plot plot) {
		XYPlot plotXY = (XYPlot) plot;

		plotXY.setOrientation(PlotOrientation.VERTICAL);

		plotXY.setDomainCrosshairVisible(true);
		plotXY.setDomainCrosshairLockedOnData(false);
		plotXY.setRangeCrosshairVisible(false);
	}

}
