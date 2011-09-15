package de.lmu.ifi.dbs.knowing.core.validation

import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.{ OSGIUtil, ResultsUtil }
import de.lmu.ifi.dbs.knowing.core.events._
import java.util.Properties
import weka.core.{ Instance, Instances }
import com.eaio.uuid.UUID

class XCrossValidator(var factory: TFactory, var folds: Int, var validator_properties: Properties) extends TProcessor {

  protected var results: List[Instances] = Nil
  protected var resultsMap: Map[UUID, (Int, Instances)] = Map()
  protected var classLabels: Array[String] = Array()
  protected var currentFold: Int = 0

  private var first_run = true
  //private var sorted = true

  def this() = this(null, 10, new Properties)

  override def customReceive = {
    case status: Status => //statusChanged(status) handle it!
  }

  def build(instances: Instances) {
    //Init classlabels
    val index = guessAndSetClassLabel(instances)
    index match {
      case -1 =>
        classLabels = Array()
        warning(this, "No classLabel found in " + instances.relationName)
      case x => classLabels = classLables(instances.attribute(x))
    }
    //Create crossValidator actors for each fold

    val crossValidators = initCrossValidators(folds)
    debug(this, "Fold-Actors created!")
    statusChanged(Progress("validation", 0, folds))
    startCrossValidation(crossValidators, instances)

    debug(this, "Fold-Actors configured and training started")
  }

  def result(result: Instances, query: Instance) {

    results = result :: results
    currentFold += 1
    if (currentFold == folds) {
      debug(this, "Last Fold " + currentFold + " results arrived")
      sendEvent(Results(mergeResults))
      currentFold = 0
    } else {
      statusChanged(Progress("validation", 1, folds))
      debug(this, "Fold " + currentFold + " results arrived")
    }

  }

  protected def initCrossValidators(folds: Int) = for (i <- 0 until folds; val actor = factory.getInstance) yield actor

  protected def startCrossValidation(crossValidators: IndexedSeq[ActorRef], instances: Instances) {
    for (j <- 0 until folds) {
      self startLink crossValidators(j) //Start actors and link yourself as supervisor
      crossValidators(j) ! Register(self, None) //Register so results/status events are send to us
      crossValidators(j) ! Configure(configureProperties(validator_properties, j)) //Configure actor
      crossValidators(j) ! Results(instances.trainCV(folds, j)) //Send the train set
      crossValidators(j) ! Queries(instances.testCV(folds, j)) //Query the trained crossValidator instance
    }
  }

  /**
   * Merge two matrices. Skips zero-values
   */
  protected def mergeResults: Instances = ResultsUtil.appendInstances(new Instances(results(0), results.size * 100), results)

  //def build

  /*sorted match {
      case true => startCrossValidationSorted(crossValidators, instances)
      case false => startCrossValidation(crossValidators, instances)
    } */

  //def result

  /*sorted match {
      case true =>
        self.sender match {
          case Some(s) =>
            val entry = resultsMap(s.getUuid)
            val index = entry._1
            resultsMap += (s.getUuid -> (index, result))
          case None => //Nothing
        }
      case false => results = result :: results
    } */

  /*sorted match {
        case true => sendEvent(Results(mergeSortedResults))
        case false => sendEvent(Results(mergeResults))
      }*/

  protected def startCrossValidationSorted(crossValidators: IndexedSeq[ActorRef], instances: Instances) {
    val empty = ResultsUtil.emptyResult
    for (j <- 0 until folds) {
      self startLink crossValidators(j) //Start actors and link yourself as supervisor
      resultsMap += (crossValidators(j).getUuid -> (j, empty))
      crossValidators(j) ! Register(self, None) //Register so results/status events are send to us
      crossValidators(j) ! Configure(configureProperties(validator_properties, j)) //Configure actor
      crossValidators(j) ! Results(instances.trainCV(folds, j)) //Send the train set
      crossValidators(j) ! Queries(instances.testCV(folds, j)) //Query the trained crossValidator instance
    }
  }

  protected def mergeSortedResults: Instances = {
    val sorted = resultsMap.toList.sortBy(_._2._1).map(_._2._2)
    ResultsUtil.appendInstances(new Instances(sorted(0), sorted.size * 100), sorted)
  }

  def configure(properties: Properties) = {
    //Retrieve CrossValidator factory
    val factory = OSGIUtil.getFactoryService(CrossValidatorFactory.id)
    factory match {
      case Some(f) => this.factory = f
      case None => throw new Exception("No Factory with " + CrossValidatorFactory.id + " found!")
    }
    //Set properties for this XCrossValidator
    val strFolds = properties.getProperty(CrossValidatorFactory.FOLDS, "10")
    folds = strFolds.toInt
    validator_properties = properties
  }

  /**
   * Creates properties for each CrossValidator-fold
   */
  protected def configureProperties(properties: Properties, fold: Int): Properties = {
    val returns = new Properties
    returns.putAll(properties)
    returns.setProperty(CrossValidatorFactory.FOLD, fold.toString)
    returns.setProperty(CrossValidatorFactory.STANDALONE, "false")
    returns
  }

  def query(query: Instance): Instances = { null }

  def getClassLabels(): Array[String] = classLabels

}

class XCrossValidatorFactory extends TFactory {

  val name: String = XCrossValidatorFactory.name
  val id: String = XCrossValidatorFactory.id

  def getInstance(): ActorRef = actorOf[XCrossValidator]

  def createDefaultProperties: Properties = {
    val props = new Properties();
    props.setProperty(CrossValidatorFactory.CLASSIFIER, "")
    props.setProperty(CrossValidatorFactory.FOLDS, "10")
    props
  }

  def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()
}

object XCrossValidatorFactory {

  val name: String = "XCrossValidator"
  val id: String = classOf[XCrossValidator].getName
}