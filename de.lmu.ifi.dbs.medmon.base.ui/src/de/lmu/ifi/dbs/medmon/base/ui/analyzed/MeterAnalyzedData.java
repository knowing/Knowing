package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.Range;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;

public class MeterAnalyzedData extends AbstractAnalyzedData {

	private List<MeterInterval> intervals = new ArrayList<MeterInterval>();
	
	private double min;
	private double max;
	
	/* ========================== */
	/* == Dataset manipulation == */
	/* ========================== */
	
	public void setNeedle(double value) {
		((DefaultValueDataset)dataset).setValue(value);
	}
	
	/* ========================== */
	/* === Plot manipulation ==== */
	/* ========================== */
	
	public void addInterval(MeterInterval interval) {
		intervals.add(interval);
	}
	
	public void setRange(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	/* ========================== */
	/* ===== Chart creation ===== */
	/* ========================== */
	
	@Override
	protected JFreeChart createChart(Dataset dataset) {
		MeterPlot plot = new MeterPlot((ValueDataset) dataset);
		JFreeChart chart = new JFreeChart("Meter Chart 1", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		ChartUtilities.applyCurrentTheme(chart);

		return chart;
	}

	@Override
	protected Dataset createDataset() {
		return new DefaultValueDataset(0.0);
	}

	@Override
	protected void configurePlot(Plot plot) {
		MeterPlot plotMeter = (MeterPlot) plot;
		plotMeter.setRange(new Range(min, max));
		for (MeterInterval i : intervals) 
			plotMeter.addInterval(i);
		
		
		plotMeter.setNeedlePaint(Color.darkGray);
		plotMeter.setDialBackgroundPaint(Color.white);
		plotMeter.setDialOutlinePaint(Color.gray);
		plotMeter.setDialShape(DialShape.CHORD);
		plotMeter.setMeterAngle(260);
		plotMeter.setTickLabelsVisible(true);
		plotMeter.setTickLabelFont(new Font("Dialog", Font.BOLD, 10));
		plotMeter.setTickLabelPaint(Color.darkGray);
		plotMeter.setTickSize(5.0);
		plotMeter.setTickPaint(Color.lightGray);

		plotMeter.setValuePaint(Color.black);
		plotMeter.setValueFont(new Font("Dialog", Font.BOLD, 14));
	}

}
