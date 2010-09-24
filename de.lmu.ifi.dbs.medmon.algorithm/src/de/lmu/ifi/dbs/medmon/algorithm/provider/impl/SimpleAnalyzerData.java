package de.lmu.ifi.dbs.medmon.algorithm.provider.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import de.lmu.ifi.dbs.medmon.algorithm.extension.IAnalyzedData;

public class SimpleAnalyzerData implements IAnalyzedData {

	
	@Override
	public void createContent(Composite parent) {
		JFreeChart chart = createChart(createDataset());
		new ChartComposite(parent, SWT.NONE, chart, true);
	}
	
	/**
	 * Creates the Dataset for the Pie chart
	 */
	private PieDataset createDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Eins", new Double(43.2));
		dataset.setValue("Zwei", new Double(10.0));
		dataset.setValue("Drei", new Double(27.5));
		dataset.setValue("Vier", new Double(17.5));
		dataset.setValue("Fuenf", new Double(11.0));
		dataset.setValue("Sechs", new Double(19.4));
		return dataset;
	}

	/**
	 * Creates the Chart based on a dataset
	 */
	private JFreeChart createChart(PieDataset dataset) {

		JFreeChart chart = ChartFactory.createPieChart("Analyzed Data", // chart
				// title
				dataset, // data
				true, // include legend
				true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionOutlinesVisible(false);
		//plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		return chart;

	}

}
