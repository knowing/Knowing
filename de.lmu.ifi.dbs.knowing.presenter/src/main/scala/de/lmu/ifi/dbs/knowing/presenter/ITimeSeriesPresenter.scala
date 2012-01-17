package de.lmu.ifi.dbs.knowing.presenter

import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.{ appendInstances, timeSeriesResult,findValueAttributesAsMap, ATTRIBUTE_TIMESTAMP }
import weka.core.{ Attribute, Instances }
import weka.core.Attribute.{ NUMERIC, NOMINAL, DATE, RELATIONAL }
import java.util.Date

trait ITimeSeriesPresenter[T] extends TPresenter[T] {

  protected var header: Instances = _
  protected var series = Map[String, Attribute]()

  protected var content: Instances = _

  /**
   * Add classes to the TimeIntervalChart
   */
  def buildSeries(series: Array[Attribute])

  /**
   *
   */
  def addPoint(date: Date,values: Array[Double])

  /**
   * Update the chart. Is called after
   * adding all time.
   */
  def update()

  /**
   * Init header, categories and add intervals
   */
  def buildPresentation(instances: Instances) {
    //Create header or add content to existing model
    isBuild match {
      case false =>
        header = new Instances(instances, 0)
        content = instances
        series = findValueAttributesAsMap(content)
        buildSeries(series.values.toArray)
      case true =>
        appendInstances(content, instances)
    }
    
    val dateAttr = instances.attribute(ATTRIBUTE_TIMESTAMP)
    
    for (i <- 0 until instances.numInstances) {
      val inst = instances.get(i)
      val values = new Array[Double](series.size)
      var index = 0
      for(attr <- series.values) {
        values(index) = inst.value(attr)
        index += 1
      }
      val date = new Date(inst.value(dateAttr).toLong)
      addPoint(date, values)      
    }
    update()
  }

}