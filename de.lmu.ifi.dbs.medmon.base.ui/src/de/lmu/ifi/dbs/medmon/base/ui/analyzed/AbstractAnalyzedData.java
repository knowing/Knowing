package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.Dataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;

public abstract class AbstractAnalyzedData implements IAnalyzedData {

	protected ChartComposite chartComposite;
	protected Dataset dataset;
	
	public AbstractAnalyzedData() {
		dataset = createDataset();
	}
	
	@Override
	public void createContent(Composite parent) {
		JFreeChart chart = createChart(dataset);
		configurePlot(chart.getPlot());
		chartComposite = new ChartComposite(parent, SWT.NONE, chart, true);
	}

	@Override
	public void dispose() {
		chartComposite.dispose();
	}
	
	protected void configurePlot(Plot plot) {
		//override for special behaviour
	}
	
	protected abstract JFreeChart createChart(Dataset dataset);
	
	protected abstract Dataset createDataset();
	
	
	

}
