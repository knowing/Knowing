package de.lmu.ifi.dbs.knowing.core.validation

import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil.getFactoryService
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

  var filterFactory: TFactory = _
  var classifierFactory: TFactory = _
  var classifierProperties = new Properties

  var folds = 2
  var fold = 0
  var standalone = true

  private var confusionMatrix: Instances = _
  private var nonZeroValues: Array[Array[Int]] = Array()
  private var classLabels: Array[String] = Array()
  private var classifier: Option[ActorRef] = None
  private var filter: Option[ActorRef] = None

  private var numInstances: Int = 0
  private var currentInst: Int = 0

  private var first_run = true

  override def customReceive = {
    case Query(q) =>
      numInstances = 1
      statusChanged(Running())
      query(q)
    //    case Queries(q, id) => queries(q)
    case status: Status => statusChanged(status)
  }

  def build(instances: Instances) {
    val index = guessAndSetClassLabel(instances)
    index match {
      case -1 =>
        classLabels = Array()
        warning(this, "No classLabel found in " + instances.relationName)
      case x =>
        classLabels = classLables(instances.attribute(x))
        nonZeroValues = new Array(classLabels.length)
        for (i <- 0 until classLabels.length) nonZeroValues(i) = new Array[Int](classLabels.length).map(_ => 1)
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

  /**
   * <p> Process results. Merge with confusionMatrix </p>
   */
  def result(result: Instances, query: Instance) {
    //Assume n*n matrix, |labels|==|instances|
    if (result.size != classLabels.length)
      warning(this, "ConfusionMatrix doesn't fit to result data")
    val prob_attribute = result.attribute(ResultsUtil.ATTRIBUTE_PROBABILITY)

    val classIndex = query.classIndex
    val col = query.value(classIndex) toInt

    for (row <- 0 until result.size) {
      val entry = confusionMatrix.instance(row)
      val new_value = result.instance(row).value(prob_attribute)
      val old_value = entry.value(col)
      val value = new_value match {
        case 0 => old_value
        case x =>
          nonZeroValues(row)(col) = nonZeroValues(row)(col) + 1
          x + old_value
      }
      entry.setValue(col, value)
    }
    debug(this, currentInst + "/" + numInstances + " of [" + fold + "/" + folds + "]")
    currentInst += 1
    //Send Results if currentInst processed is the total numInstances
    if (currentInst == numInstances) {
      sendEvent(QueryResults(mergeResults, query))
      debug(this, "[" + fold + "/" + folds + "]" + confusionMatrix)
      numInstances = 0
      currentInst = 0
      statusChanged(Ready())
    }
  }

  /**
   * <p>Override queries, because query is forwarded to classifier</p>
   *
   * @param queries - queries forwared to classifier
   * @return always Nil (empty list)
   */
  override def queries(queries: Instances): List[(Instances, Instance)] = {
    statusChanged(Running())
    numInstances = queries.numInstances
    val enum = queries.enumerateInstances
    while (enum.hasMoreElements) query(enum.nextElement.asInstanceOf[Instance])
    Nil
  }

  /**
   * <p>Forward query to classifier and process results in result method</p>
   *
   * @param query - forwarded to classifier
   * @returns Instances - ConfusionMatrix
   */
  def query(query: Instance): Instances = {
    classifier match {
      case None => warning(this, "No classifier found")
      case Some(c) => c ! Query(query)
    }
    confusionMatrix
  }

  def mergeResults: Instances = {
    val size = confusionMatrix.numInstances
    for (row <- 0 until size) {
      val entry = confusionMatrix.instance(row)
      for (col <- 0 until size) {
        val value = (entry.value(col) / nonZeroValues(row)(col)) * 100
        entry.setValue(col, value)
      }
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
    classifierProperties.putAll(properties)
    classifierProperties.remove(CrossValidatorFactory.CLASSIFIER)
    classifierProperties.remove(CrossValidatorFactory.FILTER)
    classifierProperties.remove(CrossValidatorFactory.FOLDS)
    classifierProperties.remove(CrossValidatorFactory.FOLD)
    classifierProperties.remove(CrossValidatorFactory.STANDALONE)
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
    props.setProperty(CrossValidatorFactory.FILTER, "")
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
  val FILTER = "filter"
  val FOLD = "fold"
  val FOLDS = "folds"
  val STANDALONE = "standalone"
}