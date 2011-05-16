package de.lmu.ifi.dbs.knowing.core.validation

import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.Util.getFactoryService
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import java.util.Properties
import weka.core.{ Attribute, Instance, Instances }
import akka.event.EventHandler

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 13.05.2011
 *
 */
class CrossValidator(var factory: TFactory, var folds: Int, var fold: Int, var classifier_properties: Properties) extends TProcessor {

  var confusionMatrix: Instances = _
  var classLabels: Array[String] = Array()
  var classifier: Option[ActorRef] = None

  def this() = this(null, 2, 1, new Properties)

  def build(instances: Instances) = {
    //Results from classifier
    if (ResultsUtil.isClassAndProbabilityResult(instances)) {
      //Assume n*n matrix, |lables|==|instances|
      if (instances.size != classLabels.length)
        EventHandler.warning(this, "ConfusionMatrix doesn't fit to result data")
      val index = highestProbability(instances)
      val entry = confusionMatrix.get(index)
      for (i <- 0 until instances.size) {
        val result = instances.get(i)
        //TODO CrossValidator doesn't really work!
        val v = entry.value(i)
        val t = result.value(1)
        entry.setValue(i, (v + t) / 2)
      }
      sendEvent(Results(confusionMatrix))
    } else {
      buildClassifier(instances) //Input data
    }

  }

  /**
   *
   */
  private def buildClassifier(instances: Instances) {
    val index = guessAndSetClassLabel(instances)
    index match {
      case -1 =>
        classLabels = Array()
        EventHandler.warning(this, "No classLabel found in " + instances.relationName)
      case x => classLabels = classLables(instances.attribute(x))
    }
    classifier match {
      case Some(c) => c stop
      case None =>
    }
    classifier = Some(factory.getInstance.start)
    classifier.get !! Configure(classifier_properties)
    classifier.get ! Results(instances.trainCV(folds, fold))
    confusionMatrix = ResultsUtil.crossValidation(getClassLabels.toList)

    classifier.get ! Queries(instances.testCV(folds, fold))
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

  /**
   *
   */
  def query(query: Instance): Instances = {
    classifier match {
      case None => EventHandler.warning(this, "No classifier found")
      case Some(c) => c forward Query(query)
    }
    confusionMatrix
  }

  def getClassLabels(): Array[String] = classLabels

  def configure(properties: Properties) = {
    val factoryId = properties.getProperty(XCrossValidatorFactory.CLASSIFIER)
    val factory = getFactoryService(factoryId)
    factory match {
      case Some(f) => this.factory = f
      case None => throw new Exception("No Factory with " + factoryId + " found!")
    }
    val strFolds = properties.getProperty(CrossValidatorFactory.FOLDS, "10")
    val strFold = properties.getProperty(CrossValidatorFactory.FOLD, "1")
    folds = strFolds.toInt
    fold = strFold.toInt
    properties.remove(CrossValidatorFactory.CLASSIFIER)
    properties.remove(CrossValidatorFactory.FOLDS)
    classifier_properties = properties
  }

}

class CrossValidatorFactory extends TFactory {

  val name: String = CrossValidatorFactory.name
  val id: String = CrossValidatorFactory.id

  def getInstance(): ActorRef = actorOf[CrossValidator]

  def createDefaultProperties: Properties = {
    val props = new Properties();
    props.setProperty(CrossValidatorFactory.CLASSIFIER, "")
    props.setProperty(CrossValidatorFactory.FOLD, "1")
    props.setProperty(CrossValidatorFactory.FOLDS, "2")
    props
  }

  def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()
}

object CrossValidatorFactory {

  val name: String = "CrossValidator"
  val id: String = classOf[CrossValidator].getName
  val CLASSIFIER = "classifier"
  val FOLD = "fold"
  val FOLDS = "folds"
}