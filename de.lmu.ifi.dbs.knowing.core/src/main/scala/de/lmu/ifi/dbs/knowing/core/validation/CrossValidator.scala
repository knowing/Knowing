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
import scala.collection.mutable.{ Map => MutableMap }
import java.util.ArrayList

/**
 * Single CrossValidation step
 * 
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

  //TODO flags are not really perfect. Some have double meaning.

  private var resultInstances = MutableMap[Instance, Instances]()
  private var nonZeroValues: Array[Array[Int]] = Array()
  private var classLabels: Array[String] = Array()
  private var classifier: Option[ActorRef] = None
  private var classifierTrained = false
  private var filter: Option[ActorRef] = None
  private var filterTrained = false
  private var queriesFiltered = false

  /** Instances to train classifier with filtered train-data */
  private var numInstancesTrain = 0
  private var currentInstTrain = 0
  private var filteredTrainData: Instances = _

  private var numInstancesTest = 0
  private var currentInstTest = 0
  private var filteredTestData: Instances = _

  private var first_run = true

  override def customReceive = {
    case Query(q) =>
      numInstancesTest = 1
      statusChanged(Running())
      query(q)
    case status: Status => statusChanged(status)
  }

  def build(instances: Instances) {
    //    debug(this, "Build CrossValidator " + instances.relationName)
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
        filter.get.dispatcher = self.dispatcher
        self startLink filter.get
        filter.get ! Configure(classifierProperties)
    }

    //Init classifier
    classifier = Some(classifierFactory.getInstance)
    classifier.get.dispatcher = self.dispatcher
    self startLink classifier.get
    classifier.get ! Configure(classifierProperties)

    //Start with filter or directly the classifier
    filterTrained match {
      case true =>
        //No filter, classifier gets trained directly
        debug(this, "Build CrossValidator[unfiltered] with " + instances.relationName)
        classifierTrained = true
        queriesFiltered = true
        startValidation(instances, classifier.get)
      case false =>
        debug(this, "Build CrossValidator[filtered] with" + instances.relationName)
        startValidation(instances, filter.get, true)
    }
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
   * Process results. Merge with confusionMatrix.
   *
   * The results identified by the flags classifierTrained, queriesFiltered and
   * the number of train and test instances have been received.
   * If no filter is specified, filterTrained and queriesFiltered are automatically true.
   *
   * Data flow [unfiltered / no standalone]
   * [1] classifier ! Results(train)
   * [2] forward all queries directly to the classifier, without caching. Store numInstancesTest
   * [3] Receive results, wait for the last instances to arrive, merge and send Results
   *
   * Data flow [filtered / no standalone]
   * [1] filter ! Results(train) -> train filter
   * [2] filter ! Queries(train) -> filter classifier train data. Store numInstancesTrain
   * [3] Receive results, wait for last trained instances and than filter testInstances.
   * [4] Receive results, wait for last trained instances and then query classifier.
   * [5] Receive results, wait for last classified instance, merge and send Results
   *
   *
   */
  def result(result: Instances, query: Instance) {

    val CurrentTrain = currentInstTrain + 1
    val CurrentTest = currentInstTest + 1
    (classifierTrained, queriesFiltered) match {

      // Nothing trained or filtered yet
      case (false, false) => numInstancesTrain match {
        case CurrentTrain =>
          classifier.get ! Results(filteredTrainData)
          filterTrained = true
          classifierTrained = true
          numInstancesTrain = cacheSize
          processStoredQueries
        case _ =>
          // If training data isn't completely filtered yet
          // Create if not existed
          if (filteredTrainData == null) filteredTrainData = new Instances(result, numInstancesTrain)

          val enum = result.enumerateInstances
          while (enum.hasMoreElements) filteredTrainData.add(enum.nextElement.asInstanceOf[Instance])
          currentInstTrain += 1
        // If training data isn't completely filtered yet
      }

      // Classifier was trained with filtered data, filter test data
      case (true, false) => numInstancesTest match {
        case CurrentTest =>
          queriesFiltered = true
          currentInstTest = 0
          numInstancesTest = filteredTestData.numInstances
          classifier.get ! Queries(filteredTestData)
        case _ =>
          // If testing data isn't completely filtered yet
          // Create if not existed
          if (filteredTestData == null) filteredTestData = new Instances(result, numInstancesTest)

          val enum = result.enumerateInstances
          while (enum.hasMoreElements) filteredTestData.add(enum.nextElement.asInstanceOf[Instance])
          currentInstTest += 1
      }

      // Classifier trained and test data filtered
      case (true, true) =>
        resultInstances += (query -> result)

        currentInstTest += 1
        // Send Results if currentInst processed is the total numInstances
        if (currentInstTest == numInstancesTest) {
          val header = new Instances(query.dataset, numInstancesTest)
          val results = ResultsUtil.appendClassDistribution(header, resultInstances.toMap)
          sendEvent(QueryResults(results, query))
          numInstancesTest = 0
          currentInstTest = 0
          resultInstances = MutableMap()
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
  override def queries(queries: Instances): List[(Instance, Instances)] = {
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
    (filter, filterTrained, classifier, classifierTrained, queriesFiltered) match {
      case (_, _, None, _, _) => warning(this, "No classifier found")
      //cache if filtered isn't trained yet
      case (Some(f), false, Some(_), _, _) => cacheQuery(query)
      //forward to filter if exists
      case (Some(f), true, Some(c), false, _) => f ! Query(query)
      case (Some(f), true, Some(c), _, false) => f ! Query(query)
      //forward directly to classifier
      case (_, _, Some(c), true, true) => c ! Query(query)
    }
    null
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
        case Some(_) => queries(e._2.queries)
      }
    }
  }

  private def createHeader(query: Instance, result: Instances): Instances = {
    //Generate AttributeList
    val attr = new ArrayList[Attribute](query.numAttributes + result.numAttributes)
    val qAttr = query.enumerateAttributes
    while (qAttr.hasMoreElements) {
      attr.add(qAttr.nextElement.asInstanceOf[Attribute])
    }

    val ret = new Instances(ResultsUtil.NAME_CLASS_DISTRIBUTION, attr, numInstancesTest)
    debug(this, "Attributes: " + query.numAttributes + " / " + result.numAttributes)
    debug(this, "Header: " + ret)
    guessAndSetClassLabel(ret)
    ret
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

  private def cacheSize: Int = {
    val size = queriesQueue.foldLeft(0)((size, q) => size + q._2.queries.numInstances)
    val completeSize = size + queryQueue.size
    completeSize
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