package de.sendsor.accelerationSensor.algorithm;

import java.awt.Color;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAnalyzedData;

public class SimpleAnalyzerData implements IAnalyzedData {

	private static IntervalXYDataset dataset;
	private static XYIntervalSeries[] categories;
		
	public SimpleAnalyzerData(IntervalXYDataset dataset) {
		this.dataset = dataset;
	}
	
	public static SimpleAnalyzerData getInstance() {
		dataset = createDataset();
		return new SimpleAnalyzerData(dataset);
	}

	@Override
	public void createContent(Composite parent) {
		JFreeChart chart = createChart(dataset);
		new ChartComposite(parent, SWT.NONE, chart, true);
	}

	private JFreeChart createChart(IntervalXYDataset dataset) {

		JFreeChart chart = ChartFactory.createXYBarChart("Bewegungsanalyse", "Date", true, "Y", dataset,
				PlotOrientation.HORIZONTAL, true, false, false);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setRangeAxis(new DateAxis("Date"));
		SymbolAxis xAxis = new SymbolAxis("Kategorien", getCategories());
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
	
	public void addPeriod(RegularTimePeriod p0, RegularTimePeriod p1, Category category) {
		int index = category.ordinal();
		XYIntervalSeries series = categories[index];
		series.add(index, index - 0.45, index + 0.45,
				p0.getFirstMillisecond(),
				p0.getFirstMillisecond(),
				p1.getLastMillisecond());
	}
	
	public void addPeriod(RegularTimePeriod p0, Category category) {
		addPeriod(p0,p0, category);
	}
	
	/**
	 * Creates Dataset based on the Enumeration {@link Category}
	 * @return
	 */
	private static IntervalXYDataset createDataset() {
		XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();
		Category[] cats = Category.values();
		categories = new XYIntervalSeries[cats.length];	
		for(Category category : cats) {
			XYIntervalSeries series = new XYIntervalSeries(category.name());
			categories[category.ordinal()] = series;
			dataset.addSeries(series);			
		}
		
		return dataset;
	}
	
	/**
	 * The category names
	 * @return
	 */
	private String[] getCategories() {
		Category[] cats = Category.values();
		String[] returns = new String[cats.length];
		for(Category category : cats) 
			returns[category.ordinal()] = category.name();
		return returns;
	}	

}
