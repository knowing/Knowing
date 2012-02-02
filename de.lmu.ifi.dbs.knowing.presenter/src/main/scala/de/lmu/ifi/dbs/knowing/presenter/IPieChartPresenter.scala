package de.lmu.ifi.dbs.knowing.presenter

import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.{ appendInstances, ATTRIBUTE_CLASS,ATTRIBUTE_PROBABILITY, classAndProbabilityResult}
import weka.core.Instances

trait IPieChartPresenter[T] extends TPresenter[T] {

  protected var header: Instances = _
  protected var content: Instances = _

  /**
   * Adds or updates a piece of the PieChart.
   */
  def addOrUpdatePiece(label: String, value: Double)

  /**
   * Update PieChart
   */
  def update()

  override def buildPresentation(instances: Instances) = {
    //Create header or add content to existing model
    isBuild match {
      case false =>
        header = new Instances(instances, 0)
        content = instances
      case true =>
        appendInstances(content, instances)
    }

    val classAttr = instances.attribute(ATTRIBUTE_CLASS)
    val valueAttr = instances.attribute(ATTRIBUTE_PROBABILITY)

    instances.setClass(classAttr)
    for (i <- 0 until instances.numInstances) {
      val inst = instances.get(i)
      val label = classAttr.value(inst.classValue.toInt)
      val value = inst.value(valueAttr)
      addOrUpdatePiece(label, value)
    }
    update()
  }
}

object IPieChartPresenter {
  
  import java.util.{List => JList}
  
  /**
   * <p>
   * Nominal | Numeric <br>
   * ################# <br>
   * Class	 | value   <br>
   * </p>
   * 
   * Uses ResultsUtil.classAndProbabilityResult
   * @param classes - pieChart pieces
   * @return fitting dataset scheme
   */
  def newInstances(classes: List[String]):Instances = classAndProbabilityResult(classes)
  
  /**
   * @see ResultsUtil.classAndProbability(classes)
   */
  def newInstances(classes: JList[String]):Instances = classAndProbabilityResult(classes)
}