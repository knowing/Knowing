package de.lmu.ifi.dbs.knowing.core.swt.charts

import java.util.{ Properties, Date }
import java.awt.Color
import scala.collection.mutable.{ Map => MutableMap }
import org.eclipse.swt.widgets.Composite
import org.jfree.chart.{ JFreeChart, ChartFactory }
import org.jfree.chart.plot.{ Plot, XYPlot, PlotOrientation }
import org.jfree.chart.axis.{ SymbolAxis, DateAxis }
import org.jfree.chart.renderer.xy.XYBarRenderer
import org.jfree.data.general.Dataset
import org.jfree.data.xy.{ IntervalXYDataset, XYIntervalSeriesCollection, XYIntervalSeries }
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.presenter.{PresenterFactory,ITimeIntervalClassPresenter}
import weka.core.{ Instance, Instances }
import weka.core.Utils
import de.lmu.ifi.dbs.knowing.core.results.ResultsType

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 29.05.2011
 *
 */
class TimeIntervalClassPresenter extends AbstractChartPresenter("Time Interval Class Presenter") with ITimeIntervalClassPresenter[Composite] {

  private var series: MutableMap[String, (XYIntervalSeries, Int)] = null

  protected def createChart(dataset: Dataset): JFreeChart = {
    ChartFactory.createXYBarChart(name, "Date", true, "Classes",
      dataset.asInstanceOf[IntervalXYDataset], PlotOrientation.HORIZONTAL, true, false, false)
  }

  protected def createDataset(): Dataset = new XYIntervalSeriesCollection

  override def configurePlot(plot: Plot) {
    val xyplot = plot.asInstanceOf[XYPlot]
    //Date Axis
    xyplot.setRangeAxis(new DateAxis("Date"))

    //Labels Axis
    val xAxis = new SymbolAxis("Labels", Array())
    xAxis.setGridBandsVisible(false)
    xyplot.setDomainAxis(xAxis)

    //Renderer
    val renderer = xyplot.getRenderer.asInstanceOf[XYBarRenderer]
    renderer.setUseYInterval(true)
    xyplot.setRenderer(renderer)
    xyplot.setBackgroundPaint(Color.lightGray)
    xyplot.setDomainGridlinePaint(Color.white)
    xyplot.setRangeGridlinePaint(Color.white)
  }

  /**
   * Add classes to the TimeIntervalChart
   */
  def buildCategories(classes: Array[String]) {
    series = MutableMap()
    var index = 0
    var labels: List[String] = Nil
    for (clazz <- classes) {
      val s = new XYIntervalSeries(clazz.asInstanceOf[String])
      //        dataset.asInstanceOf[XYIntervalSeriesCollection].addSeries(s)
      series += (clazz -> (s, index))
      index += 1
      labels = clazz :: labels
      dataset.asInstanceOf[XYIntervalSeriesCollection].addSeries(s)
    }
    
    //Add unclassified interval
    labels = ResultsType.UNCLASSIFIED_LABEL :: labels
    val s = new XYIntervalSeries(ResultsType.UNCLASSIFIED_LABEL)
    series += (ResultsType.UNCLASSIFIED_LABEL -> (s, index))
    dataset.asInstanceOf[XYIntervalSeriesCollection].addSeries(s)
    
    //Update DomainAxis with labels
    val xyplot = plot.asInstanceOf[XYPlot]
    val xAxisLabel = xyplot.getDomainAxis().getLabel
    val xAxis = new SymbolAxis(xAxisLabel, labels.reverse.toArray)
    xAxis.setGridBandsVisible(false)
    xyplot.setDomainAxis(xAxis)

  }

  /**
   *
   */
  def addInterval(clazz: String, from: Date, to: Date) {
    val value = series(clazz)
    //TODO TimeIntervalClassPresenter -> Compute interval size!
    value match {
      case (s, index) => addItem(s, index, from, to)
      case _ => log.warning("Unkown value")
    }
  }

  /**
   * Update chart
   */
  def update() = updateChart()

  /**
   * Adds an item to the series 
   * @param s - series to add
   * @param p0 - start period
   * @param p1 - end period
   * @param index <-> class index to determine category bar
   */
  def addItem(s: XYIntervalSeries,index: Int, from: Date, to:Date) {
    s.add(index, index - 0.25, index + 0.25, from.getTime, from.getTime, to.getTime)
  }

}

object TimeIntervalClassPresenter {
  val name = "Time Interval Class Presenter"
}

class TimeIntervalClassPresenterFactory extends PresenterFactory(classOf[TimeIntervalClassPresenter], classOf[ITimeIntervalClassPresenter[Composite]])