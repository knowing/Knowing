package de.lmu.ifi.dbs.knowing.core.swt.charts

import java.util.{ Properties, Date }
import java.awt.BasicStroke
import scala.collection.mutable.{ Map => MutableMap }
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import org.jfree.chart.{ JFreeChart, ChartFactory }
import org.jfree.data.xy.XYDataset
import org.jfree.chart.plot.{ Plot, XYPlot }
import org.jfree.data.general.Dataset
import org.jfree.data.time.{ TimeSeriesCollection, TimeSeries, Second }

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
    ChartFactory.createTimeSeriesChart(name, "Day", "Value",
      dataset.asInstanceOf[XYDataset], false, false, false)
  }

  protected def createDataset(): Dataset = new TimeSeriesCollection

  override def configurePlot(plot: Plot) {
    val xyplot = plot.asInstanceOf[XYPlot]
    val renderer = new XYLineAndShapeRenderer();
    renderer.setBaseShapesVisible(false);
    renderer.setSeriesStroke(0, new BasicStroke(
      0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
      5.0f, Array(5.0f, 10.0f), 0.0f));
    xyplot.setRenderer(renderer);
  }

  def buildContent(instances: Instances) = {
    //TODO TimeSeriesPresenter -> Check for right Instances format
    guessAndSetClassLabel(instances)
    //First buildContent call
    if (series == null) {
      series = MutableMap()
      val values = ResultsUtil.findValueAttributesAsMap(instances)
      values foreach {
        case (name, attribute) =>
          val s = new TimeSeries(name) //Create TimeSeries with META_ATTRIBUTE_NAME value
          dataset.asInstanceOf[TimeSeriesCollection].addSeries(s)
          series += (attribute.name -> s) //Store internally with real name
      }
    }

    //Fill content
    val enum = instances.enumerateInstances
    while (enum.hasMoreElements) {
      val inst = enum.nextElement.asInstanceOf[Instance]
      val dateTime = inst.value(instances.attribute(ResultsUtil.ATTRIBUTE_TIMESTAMP))
      val date = new Date(dateTime.toLong)
      //Add value to every corresponing TimeSeries
      series foreach {
        case (name, s) =>
          val value = inst.value(instances.attribute(name))
          s.add(new Second(date), value)
      }
    }
    updateChart
  }

  def configure(properties: Properties) {}

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