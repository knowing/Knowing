package de.lmu.ifi.dbs.knowing.core.swt.charts

import java.util.{ Properties, Date }
import java.awt.BasicStroke
import scala.collection.mutable.{ Map => MutableMap }
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import org.eclipse.swt.widgets.Listener
import org.jfree.chart.{ JFreeChart, ChartFactory }
import org.jfree.data.xy.XYDataset
import org.jfree.chart.plot.{ Plot, XYPlot }
import org.jfree.data.general.Dataset
import org.jfree.data.time.{ TimeSeriesCollection, TimeSeries, Second, Millisecond }
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import weka.core.{ Instances, Instance, Attribute }


/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 30.05.2011
 *
 */
class TimeSeriesPresenter extends AbstractChartPresenter("Time Series Presenter") {

  private var series: MutableMap[String, TimeSeries] = null

  protected def createChart(dataset: Dataset): JFreeChart = {
    ChartFactory.createTimeSeriesChart(name, "", "",
      dataset.asInstanceOf[XYDataset], false, false, false)
  }

  protected def createDataset(): Dataset = new TimeSeriesCollection

  def buildContent(instances: Instances) = {
    debug(this, "Create content in TimeSeries: " + instances.relationName)
    //TODO TimeSeriesPresenter -> Check for right Instances format
    guessAndSetClassLabel(instances)
    //First buildContent call
    val dataset = this.dataset.asInstanceOf[TimeSeriesCollection]
    if (series == null) {
      initSeries(instances)
    } else {
      series foreach { case (name, s) => dataset.removeSeries(s) }
    }

    //Fill content
    val enum = instances.enumerateInstances
    val numInst = instances.numInstances;
    var i = 0
    var last = 0
    debug(this, "Compute TimeSeries model with " + numInst + " instances")
    debug(this, "[                    ][0%]")
    while (enum.hasMoreElements) {
      val inst = enum.nextElement.asInstanceOf[Instance]
      val dateTime = inst.value(instances.attribute(ResultsUtil.ATTRIBUTE_TIMESTAMP))
      val date = new Date(dateTime.toLong)
      //Add value to every corresponing TimeSeries
      series foreach {
        case (name, s) =>
          val value = inst.value(instances.attribute(name))
          s.add(new Millisecond(date), value)
      }
      last = printProgress(i, numInst, last)
      i += 1
    }

    series foreach { case (name, s) => dataset.addSeries(s) }
    updateChart
  }
  
  override def configurePlot(plot: Plot) {
    val xyplot = plot.asInstanceOf[XYPlot]
    xyplot.setDomainCrosshairVisible(true);
  }

  def configure(properties: Properties) {}
  
  /**
   * <p>Initialize TimeSeries internal model</p>
   */
  private def initSeries(instances: Instances) {
    series = MutableMap()
    val values = ResultsUtil.findValueAttributesAsMap(instances)
    values foreach {
      case (name, attribute) =>
        val s = new TimeSeries(name) //Create TimeSeries with META_ATTRIBUTE_NAME value
        series += (attribute.name -> s) //Store internally with real name
        debug(this, "Added attribute in TimeSeries: " + attribute.name)
    }
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

class TimeSeriesPresenterFactory extends TFactory {

  val name: String = TimeSeriesPresenter.name
  val id: String = classOf[TimeSeriesPresenter].getName

  def getInstance(): ActorRef = actorOf[TimeSeriesPresenter]

  def createDefaultProperties: Properties = new Properties

  def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()
}