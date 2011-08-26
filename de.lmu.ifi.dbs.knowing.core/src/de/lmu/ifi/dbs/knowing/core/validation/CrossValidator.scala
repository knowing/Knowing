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
  private var classifierTrained = false
  private var filter: Option[ActorRef] = None
  private var filterTrained = false

  /** Instances to train classifier with filtered train-data */
  private var numInstancesTrain = 0
  private var currentInstTrain = 0
  private var filteredTrainData: Instances = _

  private var numInstancesTest = 0
  private var currentInstTest = 0

  private var first_run = true

  override def customReceive = {
    case Query(q) =>
      numInstancesTest = 1
      statusChanged(Running())
      query(q)
    case status: Status => statusChanged(status)
  }

  def build(instances: Instances) {
    debug(this, "Build CrossValidator " + instances.relationName)
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
    //Clean up
    (classifier, filter) match {
      case (Some(c), Some(f)) => c stop; f stop
      case (Some(c), None) => c stop
      case (None, None) =>
    }
    //Init filter if available
    filterFactory match {
      case null => filterTrained = true
      case f =>
        filter = Some(filterFactory.getInstance)
        self startLink filter.get
        filter.get !! Configure(classifierProperties)
    }

    //Init classifier
    classifier = Some(classifierFactory.getInstance)
    self startLink classifier.get
    classifier.get !! Configure(classifierProperties)

    //Start with filter or directly the classifier
    filterTrained match {
      case true =>
        //No filter, classifier gets trained directly
        classifierTrained = true
        startValidation(instances, classifier.get)
      case false =>
        startValidation(instances, filter.get, true)
    }
    confusionMatrix = ResultsUtil.confusionMatrix(getClassLabels.toList)
  }

  /**
   * <p> Process results. Merge with confusionMatrix </p>
   */
  def result(result: Instances, query: Instance) {
    // If training data isn't completly filtered yet
    if (numInstancesTrain != currentInstTrain && !classifierTrained) {
      //Create if not existed
      if (filteredTrainData == null) filteredTrainData = new Instances(result, numInstancesTrain)

      val enum = result.enumerateInstances
      while (enum.hasMoreElements) filteredTrainData.add(enum.nextElement.asInstanceOf[Instance])
      currentInstTrain += 1
    }

    var lastTrainResult = false
    //Training data completely filtered, train classifier
    if (numInstancesTrain == currentInstTrain && !classifierTrained) {
      classifier.get ! Results(filteredTrainData)
      filterTrained = true
      classifierTrained = true
      lastTrainResult = true
      processStoredQueries
    }

    if (classifierTrained && !lastTrainResult) {
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
      // debug(this, currentInst + "/" + numInstances + " of [" + fold + "/" + folds + "]")
      currentInstTest += 1
      //Send Results if currentInst processed is the total numInstances
      if (currentInstTest == numInstancesTest) {
        sendEvent(QueryResults(mergeResults, query))
        // debug(this, "[" + fold + "/" + folds + "]" + confusionMatrix)
        numInstancesTest = 0
        currentInstTest = 0
        statusChanged(Ready())
      }
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
    numInstancesTest = queries.numInstances
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
    (filter, filterTrained, classifier, classifierTrained) match {
      case (_, _, None, _) => warning(this, "No classifier found")
      //cache if filtered isn't trained yet
      case (Some(f), false, Some(_), _) => //cacheQuery(query)
      //forward to filter if exists
      case (Some(f), true, Some(c), false) => f ! Query(query) 
      //forward directly to classifier
      case (_, _, Some(c), true) => c ! Query(query)
    }
    confusionMatrix
  }

  private def mergeResults: Instances = {
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

  /**
   * Standalone = true => Splits instances, train and test actor
   * Standalone = false => Trains actor with instances
   */
  private def startValidation(instances: Instances, processor: ActorRef, filtered: Boolean = false) {
    standalone match {
      case true =>
        val train = instances.trainCV(folds, fold)
        //Train filter
        processor ! Results(train)
        //Filter training data with trained filter
        if (filtered) { processor ! Queries(train) }
        val testSet = instances.testCV(folds, fold)
        numInstancesTest = testSet.numInstances
        processor ! Queries(testSet)
      case false =>
        processor ! Results(instances)
        numInstancesTrain = instances.numInstances
        //Filter training data with trained filter
        if (filtered) { processor ! Queries(instances) }
    }
  }

  /**
   * Queries stored are forwared to the classifier specified
   */
  override def processStoredQueries {
    //Does not respect arrival time
    while (queryQueue.nonEmpty) {
      val e = queryQueue.dequeue
      e._1 match {
        case None => //nothing
        case Some(_) => query(e._2.query)
      }
    }
    while (queriesQueue.nonEmpty) {
      val e = queriesQueue.dequeue
      e._1 match {
        case None => //nothing
        //TODO Id must be send 
        case Some(_) =>  queries(e._2.queries)
      }
    }
  }

  def getClassLabels(): Array[String] = classLabels

  def configure(properties: Properties) = {
    val cFactoryId = properties.getProperty(CrossValidatorFactory.CLASSIFIER)
    val cFactory = getFactoryService(cFactoryId)
    cFactory match {
      case Some(f) => classifierFactory = f
      case None => throw new Exception("No Factory with " + cFactoryId + " found!")
    }

    val fFactoryId = properties.getProperty(CrossValidatorFactory.FILTER)
    val fFactory = getFactoryService(fFactoryId)
    fFactory match {
      case Some(f) => filterFactory = f
      case None => debug(this, "Unfiltered CrossValidation")
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