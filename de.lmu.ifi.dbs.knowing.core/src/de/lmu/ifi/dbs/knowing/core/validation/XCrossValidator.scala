package de.lmu.ifi.dbs.knowing.core.validation

import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.{ Util, ResultsUtil }
import de.lmu.ifi.dbs.knowing.core.events._
import java.util.Properties
import weka.core.{ Instance, Instances }

class XCrossValidator(var factory: TFactory, var folds: Int, var validator_properties: Properties) extends TProcessor {

  protected var confusionMatrices: List[Instances] = Nil
  protected var confusionMatrixHeader: Instances = _
  protected var classLabels: Array[String] = Array()
  protected var currentFold: Int = 0

  private var first_run = true

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
    confusionMatrixHeader = ResultsUtil.confusionMatrix(getClassLabels.toList)
    val crossValidators = initCrossValidators(folds)
    debug(this, "Fold-Actors created!")
    statusChanged(Progress("validation", 0, folds))
    startCrossValidation(crossValidators, instances)
    debug(this, "Fold-Actors configured and training started")

  }

  def result(result: Instances, query: Instance) {
    if (!result.equalHeaders(confusionMatrixHeader))
      warning(this, "Model ConfusionMatrix doesn't fit ResuClt ConfusionMatrix")

    confusionMatrices = result :: confusionMatrices

    currentFold += 1
    if (currentFold == folds) {
      debug(this, "Last Fold " + currentFold + " results arrived")
      sendEvent(Results(mergeMatrices))
      currentFold = 0
    } else {
      statusChanged(Progress("validation", 1, folds))
      debug(this, "Fold " + currentFold + " results arrived")
    }

  }

  protected def initCrossValidators(folds: Int) = for (i <- 0 until folds; val actor = factory.getInstance) yield actor

  protected def startCrossValidation(crossValidators: IndexedSeq[ActorRef], instances: Instances) {
    for (j <- 0 until folds) {
      self startLink crossValidators(j)
      crossValidators(j) !! Register(self, None)
      crossValidators(j) !! Configure(configureProperties(validator_properties, j))
      crossValidators(j) ! Results(instances.trainCV(folds, j))
      crossValidators(j) ! Queries(instances.testCV(folds, j))
    }
  }

  protected def mergeMatrices: Instances = {
    val rows = confusionMatrixHeader.numInstances
    val cols = confusionMatrixHeader.numAttributes
    val nums = Array.fill(rows)(Array.fill(cols)(1))

    val merge = (inst1: Instances, inst2: Instances) => {
      for (i <- 0 until confusionMatrixHeader.numInstances) {
        for (j <- 0 until confusionMatrixHeader.numAttributes) {
          val valResult = inst1 get (i) value (j)
          val valMatrix = inst2 get (i) value (j)
          val values = (valResult, valMatrix)
          values match {
            case (0, v2) => inst2 get (i) setValue (j, v2)
            case (v1, 0) => inst2 get (i) setValue (j, v1)
            case (v1, v2) =>
              inst2 get (i) setValue (j, (v1 + v2))
              nums(i)(j) = nums(i)(j) + 1
          }
        }
      }
      inst2
    }
    val result = confusionMatrices reduceLeft ((inst1, inst2) => merge(inst1, inst2))
    for (i <- 0 until confusionMatrixHeader.numInstances) {
      for (j <- 0 until confusionMatrixHeader.numAttributes) {
        val valResult = result get (i) value (j)
        confusionMatrixHeader get (i) setValue (j, valResult / nums(i)(j))
      }
    }
    new Instances(confusionMatrixHeader)
  }

  def configure(properties: Properties) = {
    debug(this, "configure with: " + properties)
    val factory = Util.getFactoryService(CrossValidatorFactory.id)
    factory match {
      case Some(f) => this.factory = f
      case None => throw new Exception("No Factory with " + CrossValidatorFactory.id + " found!")
    }
    val strFolds = properties.getProperty(CrossValidatorFactory.FOLDS, "10")
    folds = strFolds.toInt
    validator_properties = properties
  }

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