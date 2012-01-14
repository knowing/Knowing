package de.lmu.ifi.dbs.knowing.presenter

import java.util.Date
import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.{ appendInstances, timeIntervalResult, ATTRIBUTE_CLASS, ATTRIBUTE_FROM, ATTRIBUTE_TO }
import weka.core.{ Attribute, Instances }
import weka.core.Attribute.{ NUMERIC, NOMINAL, DATE, RELATIONAL }
import akka.event.EventHandler.debug

/**
 * Provides a Presenter for the
 *
 * @author Neopmuk Seiler
 * @version 0.1
 */
trait ITimeIntervalClassPresenter[T] extends TPresenter[T] {

  protected var header: Instances = _
  protected var classes = Array[String]()

  protected var content: Instances = _

  /**
   * Add classes to the TimeIntervalChart
   */
  def buildCategories(classes: Array[String])

  /**
   *
   */
  def addInterval(clazz: String, from: Date, to: Date)
  
  /**
   * Update the chart. Is called after
   * adding all intervals.
   */
  def update()

  /**
   * Init header, categories and add intervals
   */
  def buildPresentation(instances: Instances) {
    //Create header or add content to existing model
    isBuild match {
      case false =>
        val classAttr = instances.attribute(ATTRIBUTE_CLASS)
        header = new Instances(instances, 0)
        content = instances
        classes = { for (i <- 0 until classAttr.numValues) yield classAttr.value(i)}.toArray
        buildCategories(classes)
      case true =>
        appendInstances(content, instances)
    }
    val classAttr = instances.attribute(ATTRIBUTE_CLASS)
    instances.setClass(classAttr)
    val fromAttr = instances.attribute(ATTRIBUTE_FROM)
    val toAttr = instances.attribute(ATTRIBUTE_TO)

    for (i <- 0 until instances.numInstances) {
      val inst = instances.get(i)
      val clazz = classAttr.value(inst.classValue.toInt)
      val from = new Date(inst.value(fromAttr).toLong)
      val to = new Date(inst.value(toAttr).toLong)
      addInterval(clazz, from, to)
    }
    update()
  }
}