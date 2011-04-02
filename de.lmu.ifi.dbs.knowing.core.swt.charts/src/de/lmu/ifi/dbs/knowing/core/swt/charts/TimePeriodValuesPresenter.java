/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.factory.IPresenterFactory;
import de.lmu.ifi.dbs.knowing.core.processing.IPresenter;
import de.lmu.ifi.dbs.knowing.core.query.Results;

/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 01.04.2011
 * 
 */
public class TimePeriodValuesPresenter extends AbstractChartPresenter implements IPresenterFactory {

	public static final String ID = "de.lmu.ifi.dbs.knowing.core.swt.charts.TimePeriodValuesPresenter";

	private Instances model;

	private final Map<Comparable, TimePeriodValues> seriesMap = new HashMap<Comparable, TimePeriodValues>();

	private int interval_length = 1;
	private TimeUnit unit = TimeUnit.MILLISECONDS;

	public TimePeriodValuesPresenter() {
		super("TimePeriodValuesPresenter");
	}

	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */

	@Override
	protected void createContent(Instances dataset) {
		ArrayList<Instance> instances = Collections.<Instance> list(dataset.enumerateInstances());
		Attribute timeAttribute = dataset.attribute(Results.ATTRIBUTE_TIMESTAMP);
		// Attribute valAttribute = dataset.attribute(Results.ATTRIBUTE_VALUE);
		List<Attribute> valueAttributes = Results.findValueAttributes(dataset);

		for (Instance instance : instances) {
			// Timestamp
			long timeInMillis = (long) instance.value(timeAttribute);
			
			TimePeriod period = adapt(unit, timeInMillis);
//			// Mind the gap
//			long minimum_duration = unit.toMillis(interval_length);
//			long difference = Math.abs(timeInMillis - previous_timestamp);
//			if (previous_timestamp > 0 && (2 * difference) > minimum_duration) {
//				for (Attribute attribute : valueAttributes) {
//					String name = attribute.getMetadata().getProperty(Results.META_ATTRIBUTE_NAME, attribute.name());
//					TimePeriod end = adapt(unit, previous_timestamp + minimum_duration);
//					TimePeriod start = adapt(unit, timeInMillis - minimum_duration);
//					add(name, end, 0);
//					add(name, start, 0);
//				}
//			}
//			previous_timestamp = timeInMillis;

			// Values
			for (Attribute attribute : valueAttributes) {
				double value = instance.value(attribute);
				String name = attribute.getMetadata().getProperty(Results.META_ATTRIBUTE_NAME, attribute.name());
				for (int i = 0; i < interval_length; i++) {
					add(name, period, value);
				}
			}
		}
		updateChart();
		redraw();
	}

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

	private TimePeriod adapt(TimeUnit unit, long timeInMillis) {
		if (unit.equals(TimeUnit.MILLISECONDS))
			return new Millisecond(new Date(timeInMillis));
		else if (unit.equals(TimeUnit.SECONDS))
			return new Second(new Date(timeInMillis));
		return new Millisecond(new Date(timeInMillis));
	}

	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */

	@Override
	protected JFreeChart createChart(Dataset dataset) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Time Series", "Date", "Activity", (XYDataset) dataset,
				true, true, false);
		return chart;
	}

	@Override
	protected Dataset createDataset() {
		return new TimePeriodValuesCollection();
	}

	@Override
	protected void configurePlot(Plot plot) {
		XYPlot plotXY = (XYPlot) plot;
		plotXY.setBackgroundPaint(Color.white);
		plotXY.setDomainGridlinePaint(Color.lightGray);
		plotXY.setRangeGridlinePaint(Color.lightGray);
	}

	@Override
	public Instances getModel(List<String> labels) {
		// Not really as intended!!
		if (model == null)
			model = Results.dateAndValuesResult(labels);
		return model;
	}

	/* ========================== */
	/* ======== Factory ========= */
	/* ========================== */

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public Properties getDefault() {
		return new Properties();
	}

	@Override
	public IPresenter<Composite> getInstance(Properties properties) {
		return new TimePeriodValuesPresenter();
	}

}
