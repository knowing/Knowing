package de.lmu.ifi.dbs.knowing.core.swt.charts

import java.util.{ Properties, Date }

import scala.collection.mutable.{ Map => MutableMap }

import akka.event.EventHandler.{ debug, info, warning, error }
import akka.actor.ActorRef
import akka.actor.Actor.actorOf

import org.jfree.chart.{ JFreeChart, ChartFactory }
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.general.Dataset
import org.jfree.data.xy.{ IntervalXYDataset, XYIntervalSeriesCollection, XYIntervalSeries }
import org.jfree.data.time.{ RegularTimePeriod, Second, Millisecond }

import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.factory.TFactory

import weka.core.{ Instance, Instances }

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 29.05.2011
 *
 */
class TimeIntervalClassPresenter extends AbstractChartPresenter("Time Interval Class Presenter") {

  private var series: MutableMap[String, (XYIntervalSeries, Int)] = null

  protected def createChart(dataset: Dataset): JFreeChart = {
    ChartFactory.createXYBarChart(name, "Date", true, "Classes",
      dataset.asInstanceOf[IntervalXYDataset], PlotOrientation.HORIZONTAL, true, false, false)
  }

  protected def createDataset(): Dataset = new XYIntervalSeriesCollection

  def buildContent(instances: Instances) = {
    //TODO TimeIntervalClassPresenter -> Check for right Instances format
	guessAndSetClassLabel(instances)
    //First buildContent call
    if (series == null) {
      series = MutableMap()
      val classes = instances.classAttribute.enumerateValues
      var index = 0
      while (classes.hasMoreElements) {
        val clazz = classes.nextElement.asInstanceOf[String]
        val s = new XYIntervalSeries(clazz.asInstanceOf[String])
        dataset.asInstanceOf[XYIntervalSeriesCollection].addSeries(s)
        series += (clazz -> (s, index))
        index += 1
      }

    }
    //Fill content
    val enum = instances.enumerateInstances
    val classAttr = instances.classAttribute
    val fromAttr = instances.attribute(ResultsUtil.ATTRIBUTE_FROM)
    val toAttr = instances.attribute(ResultsUtil.ATTRIBUTE_TO)
    while (enum.hasMoreElements) {
      val inst = enum.nextElement.asInstanceOf[Instance]
      val label = classAttr.value(inst.classValue.toInt)
      val from = inst.value(fromAttr)
      val to = inst.value(toAttr)

      val value = series(label)
      //TODO TimeIntervalClassPresenter -> Compute intervall size!
      value match {
        case (s, index) => addItem(s, new Second(new Date(from.toLong)), new Second(new Date(to.toLong)), index)
        case _ => warning(this, "Unkown value")
      }
    }
    updateChart
  }

  private def addItem(s: XYIntervalSeries, p0: RegularTimePeriod, p1: RegularTimePeriod, index: Int) {
    s.add(index, index - 0.25, index + 0.25, p0.getFirstMillisecond(), p0.getFirstMillisecond(), p1.getLastMillisecond())
  }

  def configure(properties: Properties) = {}

}

object TimeIntervalClassPresenter {
  val name = "Time Interval Class Presenter"
}

class TimeIntervalClassPresenterFactory extends TFactory {

  val name: String = TimeIntervalClassPresenter.name
  val id: String = classOf[TimeIntervalClassPresenter].getName

  def getInstance(): ActorRef = actorOf[TimeIntervalClassPresenter]

  def createDefaultProperties: Properties = new Properties

  def createPropertyValues: Map[String, Array[_<:Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()
}