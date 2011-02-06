package de.sendsor.accelerationSensor.algorithm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;

import org.jfree.chart.plot.MeterInterval;
import org.jfree.data.Range;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYSeries;

import de.lmu.ifi.dbs.medmon.base.ui.analyzed.BubbleAnalyzedData;
import de.lmu.ifi.dbs.medmon.base.ui.analyzed.HistogramAnalyzedData;
import de.lmu.ifi.dbs.medmon.base.ui.analyzed.IntervalBarAnalyzedData;
import de.lmu.ifi.dbs.medmon.base.ui.analyzed.MeterAnalyzedData;
import de.lmu.ifi.dbs.medmon.base.ui.analyzed.PieAnalyzedData;
import de.lmu.ifi.dbs.medmon.base.ui.analyzed.ScatterAnalyzedData;
import de.lmu.ifi.dbs.medmon.base.ui.analyzed.XYAreaAnalyedData;
import de.lmu.ifi.dbs.medmon.datamining.core.analyzed.EmptyAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.NumericParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.StringParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.AbstractAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;

public class SimpleAnalyzer extends AbstractAlgorithm {

	public static final String NAME = "Simple Analyzer";
	
	private static final String PIE_CHART = "Pie-Chart";
	private static final String BAR_CHART = "Bar-Chart";
	private static final String HIST_CHART = "Histogramm";
	private static final String BUBBLE_CHART = "Bubble-Chart";
	private static final String METER_CHART = "Meter-Chart";
	private static final String SCATTER_CHART = "Scatter-Chart";
	private static final String XY_AREA_CHART = "XY Line Chart";
		
	public SimpleAnalyzer() {
		super(NAME, INDEFINITE_DIMENSION, INDEFINITE_DIMENSION);
		init();
	}
	
	private void init() {
		NumericParameter toleranz = new NumericParameter("Toleranz", -10, 10, 0);
		StringParameter display = new StringParameter("Darstellung", new String[] {PIE_CHART, BAR_CHART}); //Obsolete
		parameters.put(toleranz.getName(), toleranz);
		parameters.put(display.getName(), display);
		
		EmptyAnalyzedData empty = new EmptyAnalyzedData();
		analyzedData.put(PIE_CHART, empty);
		analyzedData.put(BAR_CHART, empty);
		analyzedData.put(HIST_CHART, empty);
		analyzedData.put(BUBBLE_CHART, empty);
		analyzedData.put(METER_CHART, empty);
		analyzedData.put(SCATTER_CHART, empty);
		
		description = "A Simple Sample Analyzer";
		version = "0.4";
	}

	@Override
	public Map<String, IAnalyzedData> process(RawData data) {
		
		//SampleData Generation
		String[] labels = new String[] {"Laufen", "Sitzen", "Gehen"};
		IntervalBarAnalyzedData barData = new IntervalBarAnalyzedData(labels);
		createBarChart(barData);
		
		HistogramAnalyzedData histData = new HistogramAnalyzedData();
		createHistogramm(histData);
		
		
		BubbleAnalyzedData bubbleData = new BubbleAnalyzedData();
		createBubblePlot(bubbleData);
		
		MeterAnalyzedData meterData = new MeterAnalyzedData();
		createMeterPlot(meterData);
		
		ScatterAnalyzedData scatterData = new ScatterAnalyzedData();
		createScatterPlot(scatterData);
		
		PieAnalyzedData pieData = new PieAnalyzedData();
		createPiePlot(pieData);
		
		XYAreaAnalyedData xyareaData = new XYAreaAnalyedData();
		createXYAreaPlot(xyareaData);
		analyzedData.put(XY_AREA_CHART, xyareaData);

		analyzedData.put(DEFAULT_DATA, meterData);
	
		analyzedData.put(BAR_CHART, barData);
		analyzedData.put(HIST_CHART, histData);
		analyzedData.put(BUBBLE_CHART, bubbleData);
		analyzedData.put(METER_CHART, meterData);
		analyzedData.put(SCATTER_CHART, scatterData);
		analyzedData.put(PIE_CHART, pieData);
		return analyzedData;
	}
	
	@Override
	public Map<String, IAnalyzedData> process(RawData data, Map<String, IAnalyzedData> analyzedData) {
		return analyzedData;
	}
	
	@Override
	public String[] analyzedDataKeys() {
		return new String[] {XY_AREA_CHART, BAR_CHART, PIE_CHART, HIST_CHART, BUBBLE_CHART, METER_CHART, SCATTER_CHART };
	}
	
	@Override
	public boolean isTimeSensitiv() {
		return false;
	}
	
	/* ================= */
	/* == Sample Data == */
	/* ================= */
		
	private static void createBarChart(IntervalBarAnalyzedData dataset) {
		Hour[] hours = new Hour[48];
		Calendar cal = new GregorianCalendar();
		
		for(int i=0; i < hours.length; i++) {
			cal.add(Calendar.HOUR_OF_DAY, 1);
			hours[i] = new Hour(cal.getTime());
		}
		String[] labels = dataset.getLabels();
		for(int i=0; i < hours.length; i += 3)
			dataset.addPeriod(hours[i], hours[i+1], labels[0]);
		
		for(int i=0; i < hours.length; i += 7)
			dataset.addPeriod(hours[i], hours[i+2], labels[1]);
		
		
		for(int i=0; i < hours.length; i += 2)
			dataset.addPeriod(hours[i], hours[i], labels[2]);
	}
	
	private static void createHistogramm(HistogramAnalyzedData dataset) {
        double[] values = new double[1000];
        Random generator = new Random(12345678L);
        for (int i = 0; i < 1000; i++) {
            values[i] = generator.nextGaussian() + 5;
        }
        dataset.addSeries("H1", values, 100, 2.0, 8.0);
        values = new double[1000];
        for (int i = 0; i < 1000; i++) {
            values[i] = generator.nextGaussian() + 7;
        }
        dataset.addSeries("H2", values, 100, 4.0, 10.0);
	}
	
	private static void createScatterPlot(ScatterAnalyzedData dataset) {
		XYSeries s1 = new XYSeries("Series1");
		XYSeries s2 = new XYSeries("Series2");
		XYSeries s3 = new XYSeries("Series3");
		Random r = new Random(2350234L);
		for (int i = 0; i < 100; i++) {
			s1.add(r.nextDouble(), r.nextDouble());
			s2.add(r.nextDouble(), r.nextDouble());
			s3.add(r.nextDouble(), r.nextDouble());
		}
		dataset.addSeries(s1);
		dataset.addSeries(s2);
		dataset.addSeries(s3);
	}
	
	private static void createBubblePlot(BubbleAnalyzedData dataset) {
        double[] x = {2.1, 2.3, 2.3, 2.2, 2.2, 1.8, 1.8, 1.9, 2.3, 3.8};
        double[] y = {14.1, 11.1, 10.0, 8.8, 8.7, 8.4, 5.4, 4.1, 4.1, 25};
        double[] z = {2.4, 2.7, 2.7, 2.2, 2.2, 2.2, 2.1, 2.2, 1.6, 4};
        double[][] series = new double[][] { x, y, z };
        dataset.addSeries("Series 1", series);
	}
	
	private static void createMeterPlot(MeterAnalyzedData dataset) {
		dataset.setRange(0, 60);
		dataset.addInterval(new MeterInterval("Normal", new Range(0.0, 35.0), Color.lightGray, new BasicStroke(2.0f),
				new Color(0, 255, 0, 64)));
		dataset.addInterval(new MeterInterval("Warning", new Range(35.0, 50.0), Color.lightGray, new BasicStroke(2.0f),
				new Color(255, 255, 0, 64)));
		dataset.addInterval(new MeterInterval("Critical", new Range(50.0, 60.0), Color.lightGray, new BasicStroke(2.0f),
				new Color(255, 0, 0, 128)));
		dataset.setNeedle(30.0);
	}
	
	private static void createPiePlot(PieAnalyzedData dataset) {
		dataset.setValue("Zuhause", 25.0);
		dataset.setValue("Unterwegs", 60.0);
		dataset.setValue("Arbeit", 15.0);
	}
	
	private static void createXYAreaPlot(XYAreaAnalyedData dataset) {
		TimeSeries series1 = new TimeSeries("Series1");
		double value = 0.0;
		Second sec = new Second();
		for(int i=0; i < 200; i++) {
			value = value + Math.random() - 0.5;
			series1.add(sec, value);
			sec = (Second) sec.next();
		}
		dataset.addSeries(series1);
		
		for(int i=0; i < 200; i++) {
			value = value + Math.random() - 0.5;
			dataset.addNextPeriod("Series1", value);
		}
	}

}
