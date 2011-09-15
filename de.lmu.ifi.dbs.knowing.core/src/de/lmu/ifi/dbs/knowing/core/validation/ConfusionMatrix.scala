package de.lmu.ifi.dbs.knowing.core.validation

import java.util.Properties
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.{ NAME_CLASS_DISTRIBUTION, ATTRIBUTE_CLASS_DISTRIBUTION, confusionMatrix => createMatrix }
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory

import weka.core.{ Attribute, Instances, Instance }

class ConfusionMatrix extends TProcessor {

  private var confusionMatrix: Instances = _
  private var instancesCount: Array[Int] = Array()
  private var classLabels: Array[String] = Array()
  private var distAttribute: Attribute = _

  def build(instances: Instances) {
    //Init ConfusionMatrix
    init(instances)

    //Create confusion matrix
    val classIndex = instances.classIndex
    val enum = instances.enumerateInstances
    while (enum.hasMoreElements) {
      val inst = enum.nextElement.asInstanceOf[Instance]
      //distribution == ResultsUtil.classDistribution
      val clazz = ResultsUtil.highestProbability(inst)
      val col = inst.value(classIndex).toInt
      //This should be solved via a map -> only for non-distributed
      for (row <- 0 until classLabels.size if classLabels(row).equals(clazz._2)) {
        val entry = confusionMatrix.instance(row)
        val old_value = entry.value(col)
        instancesCount(col) = instancesCount(col) + 1
        entry.setValue(col, old_value + 1)
      }
    }
    
    //Merge Results
    val rows, cols = classLabels.size
    for(row <- 0 until rows) {
      val entry = confusionMatrix.instance(row)
      for(col <- 0 until cols) {
        val value = entry.value(col)
        //Avoid division with zero
        val average = instancesCount(col) match {
          case 0 => 1
          case x => x
        }
        //percent
        entry.setValue(col, (value / average) * 100)
      }
    }
    
    sendResults(confusionMatrix)
  }

  def query(query: Instance): Instances = { null }

  def result(result: Instances, query: Instance): Unit = {}

  def configure(properties: Properties): Unit = {}

  private def init(instances: Instances) {
    val index = guessAndSetClassLabel(instances)
    index match {
      case -1 =>
        classLabels = Array()
        warning(this, "No classLabel found in " + instances.relationName)
      case x =>
        classLabels = classLables(instances.attribute(x))
        instancesCount = new Array(classLabels.size)
    }
    confusionMatrix = createMatrix(classLabels.toList)
  }

  private def highestProbability(instances: Instances): Int = {
    var index = -1
    var max = -1.0
    val valAttr = instances.attribute(1)
    for (i <- 0 until instances.numInstances) {
      val inst = instances.instance(i)
      val value = inst.value(valAttr)
      if (value > max) {
        max = value
        index = i
      }
    }
    index
  }

}

class ConfusionMatrixFactory extends ProcessorFactory(classOf[ConfusionMatrix])