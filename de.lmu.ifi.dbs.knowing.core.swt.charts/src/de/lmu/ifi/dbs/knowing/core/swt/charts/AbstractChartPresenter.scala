package de.lmu.ifi.dbs.knowing.core.swt.charts

import de.lmu.ifi.dbs.knowing.core.swt.SWTPresenter
import org.jfree.data.general.Dataset
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.Plot
import org.jfree.experimental.chart.swt.ChartComposite
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.SWT
import weka.core.Instances

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 24.04.2011
 *
 */
abstract class AbstractChartPresenter(val name: String) extends SWTPresenter {

  private var chartComposite: ChartComposite = _
  private var chart: JFreeChart = _

  protected var dataset: Dataset = createDataset

  /**
   *
   */
  def createControl(parent: Composite) = {
    chart = createChart(dataset)
    configurePlot(chart.getPlot())
    chartComposite = new ChartComposite(parent, SWT.NONE, chart, true)
  }

  def updateChart {
    if (chart != null)
      chart.fireChartChanged()
  }
  /**
   * Override this method for special behaviour. It's called
   * after the dataset was created;
   * @param plot
   */
  protected def configurePlot(plot: Plot) {}

  protected def createChart(dataset: Dataset): JFreeChart

  protected def createDataset: Dataset

}