/*                                                              *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
**                                                              **
** Knowing Framework                                            **
** Apache License - http://www.apache.org/licenses/             **
** LMU Munich - Database Systems Group                          **
** http://www.dbs.ifi.lmu.de/                                   **
\*                                                              */
package de.lmu.ifi.dbs.knowing.core.swt.charts

import java.util.{ Properties, Date }
import java.awt.BasicStroke
import scala.collection.mutable.Map
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import org.eclipse.swt.widgets.{ Composite, Listener }
import org.jfree.chart.{ JFreeChart, ChartFactory }
import org.jfree.data.xy.XYDataset
import org.jfree.chart.plot.{ Plot, XYPlot }
import org.jfree.data.general.Dataset
import org.jfree.data.time.{ TimeSeriesCollection, TimeSeries, Second, Millisecond }
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.presenter.{PresenterFactory,ITimeSeriesPresenter}
import weka.core.{ Instances, Instance, Attribute }

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 30.05.2011
 *
 */
class TimeSeriesPresenter extends AbstractChartPresenter("Time Series Presenter") with ITimeSeriesPresenter[Composite] {

  private val series = Map[String, TimeSeries]()

  /* ========================== */
  /* == Dataset manipulation == */
  /* ========================== */

  /**
   * Add classes to the TimeIntervalChart
   */
  def buildSeries(series: Array[Attribute]) {
    series foreach { attribute =>
      val s = new TimeSeries(name) //Create TimeSeries with META_ATTRIBUTE_NAME value
      this.series += (attribute.name -> s) //Store internally with real name
      dataset.asInstanceOf[TimeSeriesCollection].addSeries(s)
      debug(this, "Added attribute in TimeSeries: " + attribute.name)
    }
  }

  /**
   *
   */
  def addPoint(date: Date, values: Array[Double]) {
    //Add value to every corresponing TimeSeries
    var index = 0
    series foreach {
      case (name, s) =>
        val value = values(index)
        s.add(new Millisecond(date), value)
        index += 1
    }
  }

  /**
   * Update the chart. Is called after
   * adding all time.
   */
  def update() = updateChart()

  /* ========================== */
  /* ===== Chart creation ===== */
  /* ========================== */

  protected def createChart(dataset: Dataset): JFreeChart = {
    ChartFactory.createTimeSeriesChart(name, "", "",
      dataset.asInstanceOf[XYDataset], false, false, false)
  }

  protected def createDataset(): Dataset = new TimeSeriesCollection

  override def configurePlot(plot: Plot) {
    val xyplot = plot.asInstanceOf[XYPlot]
    xyplot.setDomainCrosshairVisible(true);
  }

  /**
   * Prints progress bar to console
   */
  private def printProgress(current: Int, complete: Int, last: Int): Int = {
    val percent = (current * 100) / complete
    val dots = percent / 5
    val print = (percent % 5) == 0
    if (print && last != dots) {
      val sb = new StringBuilder
      sb append ("[")
      for (i <- 0 until dots) sb append (".")
      for (i <- dots until 20) sb append (" ")
      sb append ("][")
      sb append (percent)
      sb append ("%]")
      debug(this, sb toString)
    }
    dots
  }

}

object TimeSeriesPresenter {
  val name = "Time Interval Class Presenter"
}

class TimeSeriesPresenterFactory extends PresenterFactory(classOf[TimeSeriesPresenter], classOf[ITimeSeriesPresenter[Composite]])

