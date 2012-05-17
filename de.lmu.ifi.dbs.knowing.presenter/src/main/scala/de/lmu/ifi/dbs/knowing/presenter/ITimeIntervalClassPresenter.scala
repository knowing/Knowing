package de.lmu.ifi.dbs.knowing.presenter

import java.util.Date
import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.appendInstances
import de.lmu.ifi.dbs.knowing.core.results.TimeIntervalResults
import de.lmu.ifi.dbs.knowing.core.results.TimeIntervalResults._
import weka.core.{ Attribute, Instances }
import weka.core.Attribute.{ NUMERIC, NOMINAL, DATE, RELATIONAL }

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
  override def buildPresentation(instances: Instances) {
    //Create header or add content to existing model
    isBuild match {
      case false =>
        val classAttr = instances.attribute(ATTRIBUTE_CLASS)
        header = new Instances(instances, 0)
        content = instances
        classes = { for (i <- 0 until classAttr.numValues) yield classAttr.value(i) }.toArray
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

/**
 * @author Nepomuk Seiler
 * @version 0.1
 */
object ITimeIntervalClassPresenter {

  import java.util.{ List => JList }

  /**
   * <p>
   * Date | Date | Nominal <br>
   * ##################### <br>
   * from | to	 | class <br>
   * </p>
   * Uses ResultsUtil to generate Instances.
   *
   * @param labels
   * @return dataset fitting the ITimeIntervalClass scheme
   */
  def newInstances(classes: List[String], datePattern: String): Instances = TimeIntervalResults(classes, datePattern)

  /**
   * @see newInstances(classes, datePattern)
   */
  def newInstances(classes: List[String]): Instances = TimeIntervalResults(classes)

  /**
   * @see newInstances(classes, datePattern)
   */
  def newInstances(classes: JList[String]): Instances = TimeIntervalResults.newInstances(classes)
  
    /**
   * @see newInstances(classes, datePattern)
   */
  def newInstances(classes: JList[String], datePattern: String): Instances = TimeIntervalResults.newInstances(classes, datePattern)
}