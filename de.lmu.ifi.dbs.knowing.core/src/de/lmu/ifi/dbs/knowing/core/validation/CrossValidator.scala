package de.lmu.ifi.dbs.knowing.core.validation

import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.Util.getFactoryService
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import java.util.Properties
import weka.core.{ Attribute, Instance, Instances }

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 13.05.2011
 *
 */
class CrossValidator extends TProcessor {

  var classifierFactory: TFactory = _
  var classifierProperties = new Properties

  var folds = 2
  var fold = 0
  var standalone = true

  private var confusionMatrix: Instances = _
  private var classLabels: Array[String] = Array()
  private var classifier: Option[ActorRef] = None

  private var numInstances: Int = 0
  private var currentInst: Int = 0

  private var first_run = true

  override def customReceive = {
    case Query(q) =>
      numInstances = 1
      statusChanged(Running())
      query(q)
    case Queries(q) => queries(q)
    case status: Status => statusChanged(status)
  }

  def build(instances: Instances) {
    val index = guessAndSetClassLabel(instances)
    index match {
      case -1 =>
        classLabels = Array()
        warning(this, "No classLabel found in " + instances.relationName)
      case x => classLabels = classLables(instances.attribute(x))
    }
    classifier match {
      case Some(c) => c stop
      case None =>
    }
    classifier = Some(classifierFactory.getInstance)
    self startLink classifier.get
    classifier.get !! Configure(classifierProperties)

    standalone match {
      case true =>
        classifier.get ! Results(instances.trainCV(folds, fold))
        val testSet = instances.testCV(folds, fold)
        numInstances = testSet.numInstances
        classifier.get ! Queries(testSet)
      case false =>
        classifier.get ! Results(instances)
    }
    confusionMatrix = ResultsUtil.confusionMatrix(getClassLabels.toList)
  }

  def result(result: Instances, query: Instance) {
    //Assume n*n matrix, |lables|==|instances|
    if (result.size != classLabels.length)
      warning(this, "ConfusionMatrix doesn't fit to result data")
    val prob_attribute = result.attribute(ResultsUtil.ATTRIBUTE_PROBABILITY)

    val classIndex = query.classIndex
    val column = query.value(classIndex)

    for (i <- 0 until result.size) {
      val entry = confusionMatrix.instance(i)
      val new_value = result.instance(i).value(prob_attribute)
      val old_value = entry.value(column.toInt) / 100
      var value = 0.0
      first_run match {
        case true =>
          value = new_value
          first_run = false
        case false => value = ((new_value + old_value) / 2) * 100
      }

      entry.setValue(column.toInt, value)
    }
//    debug(this,  currentInst + "/" + numInstances + " of [" + fold + "/" + folds + "]")
    currentInst += 1
    if (currentInst == numInstances) {
      sendEvent(QueryResults(confusionMatrix, query))
//      debug(this,"[" + fold + "/" + folds + "]" + confusionMatrix)
      numInstances = 0
      currentInst = 0
      statusChanged(Ready())
    }
  }

  def queries(queries: Instances) {
    statusChanged(Running())
    numInstances = queries.numInstances 
    val enum = queries.enumerateInstances
    while (enum.hasMoreElements)
      query(enum.nextElement.asInstanceOf[Instance])
  }

  /**
   *
   */
  def query(query: Instance): Instances = {
    classifier match {
      case None => warning(this, "No classifier found")
      case Some(c) => c ! Query(query)
    }
    confusionMatrix
  }

  def getClassLabels(): Array[String] = classLabels

  def configure(properties: Properties) = {
    val factoryId = properties.getProperty(CrossValidatorFactory.CLASSIFIER)
    val factory = getFactoryService(factoryId)
    factory match {
      case Some(f) => classifierFactory = f
      case None => throw new Exception("No Factory with " + factoryId + " found!")
    }
    folds = properties.getProperty(CrossValidatorFactory.FOLDS, "10").toInt
    fold = properties.getProperty(CrossValidatorFactory.FOLD, "1").toInt
    standalone = properties.getProperty(CrossValidatorFactory.STANDALONE, "true").toBoolean

    //Remove used properties
    properties.remove(CrossValidatorFactory.CLASSIFIER)
    properties.remove(CrossValidatorFactory.FOLDS)
    properties.remove(CrossValidatorFactory.FOLD)
    properties.remove(CrossValidatorFactory.STANDALONE)
    classifierProperties = properties
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
  val STANDALONE = "standalone"
}