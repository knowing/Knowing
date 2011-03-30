package de.lmu.ifi.dbs.knowing.core.swt.charts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.Dataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import de.lmu.ifi.dbs.knowing.core.swt.SWTPresenter;

public abstract class AbstractChartPresenter extends SWTPresenter {

	private final String name;
	
	private ChartComposite chartComposite;
	private JFreeChart chart;
	
	protected Dataset dataset;
	
	public AbstractChartPresenter(String name) {
		this.name = name;
		dataset = createDataset();
	}

	@Override
	protected void createControl(Composite parent) {
		chart = createChart(dataset);
		configurePlot(chart.getPlot());
		chartComposite = new ChartComposite(parent, SWT.NONE, chart, true);
	}	
	
	public void dispose() {
		chartComposite.dispose();
	}
	
	public JFreeChart getChart() {
		return chart;
	}
	
	public void updateChart() {
		if(chart != null)
			chart.fireChartChanged();
	}

	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Override this method for special behaviour. It's called
	 * after the dataset was created;
	 * @param plot
	 */
	protected void configurePlot(Plot plot) {}
	
	protected abstract JFreeChart createChart(Dataset dataset);
	
	protected abstract Dataset createDataset();

}
