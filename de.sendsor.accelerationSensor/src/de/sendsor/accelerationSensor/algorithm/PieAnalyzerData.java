package de.sendsor.accelerationSensor.algorithm;

import java.awt.Font;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;

public class PieAnalyzerData implements IAnalyzedData {

	private DefaultPieDataset dataset = new DefaultPieDataset();
	private ChartComposite chartComposite;
	
	@Override
	public void createContent(Composite parent) {
		JFreeChart chart = createChart(dataset);
		chartComposite = new ChartComposite(parent, SWT.NONE, chart, true);
	}
	
	@Override
	public void dispose() {
		chartComposite.dispose();		
	}

	public void setValue(Comparable key, double value) {
		dataset.setValue(key, value);
	}
	
    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return a chart.
     */
    private static JFreeChart createChart(PieDataset dataset) {

        JFreeChart chart = ChartFactory.createPieChart(
            "Activity Monitoring",  // chart title
            dataset,             // data
            true,                // include legend
            true,
            false);
        TextTitle title = chart.getTitle();
        title.setToolTipText("A title tooltip!");

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        return chart;

    }

}
