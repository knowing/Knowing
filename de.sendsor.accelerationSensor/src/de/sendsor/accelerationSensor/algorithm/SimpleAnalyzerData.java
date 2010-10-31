package de.sendsor.accelerationSensor.algorithm;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAnalyzedData;

public class SimpleAnalyzerData implements IAnalyzedData {

	@Override
	public void createContent(Composite parent) {
		IntervalXYDataset categorydataset = createDataset();
		JFreeChart chart = createChart(categorydataset);
		new ChartComposite(parent, SWT.NONE, chart, true);
	}

	private JFreeChart createChart(IntervalXYDataset dataset) {

		JFreeChart chart = ChartFactory.createXYBarChart("XYBarChartDemo7", "Date", true, "Y", dataset,
				PlotOrientation.HORIZONTAL, true, false, false);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setRangeAxis(new DateAxis("Date"));
		SymbolAxis xAxis = new SymbolAxis("Series", new String[] { "S1", "S2", "S3" });
		xAxis.setGridBandsVisible(false);
		plot.setDomainAxis(xAxis);
		XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
		renderer.setUseYInterval(true);
		plot.setRenderer(renderer);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		ChartUtilities.applyCurrentTheme(chart);
		return chart;
	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return A dataset.
	 */
	private static IntervalXYDataset createDataset() {
		Day d0 = new Day(12, 6, 2007);
		Day d1 = new Day(13, 6, 2007);
		Day d2 = new Day(14, 6, 2007);
		Day d3 = new Day(15, 6, 2007);
		Day d4 = new Day(16, 6, 2007);
		Day d5 = new Day(17, 6, 2007);

		// TimeSeriesCollection col = new TimeSeriesCollection();
		// TimeSeries series = new TimeSeries(null);
		// TimeSeriesDataItem item = new TimeSeriesDataItem(period, 1.0);
		// RegularTimePeriod period = new Re

		XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();
		XYIntervalSeries s1 = new XYIntervalSeries("S1");
		XYIntervalSeries s2 = new XYIntervalSeries("S2");
		XYIntervalSeries s3 = new XYIntervalSeries("S3");
		addItem(s1, d0, d1, 0);
		addItem(s1, d3, d3, 0);
		addItem(s2, d0, d5, 1);
		addItem(s3, d2, d4, 2);
		dataset.addSeries(s1);
		dataset.addSeries(s2);
		dataset.addSeries(s3);
		return dataset;
	}

	private static void addItem(XYIntervalSeries s, RegularTimePeriod p0, RegularTimePeriod p1, int index) {
		s.add(index, index - 0.45, index + 0.45, p0.getFirstMillisecond(), p0.getFirstMillisecond(),
				p1.getLastMillisecond());
	}

}
