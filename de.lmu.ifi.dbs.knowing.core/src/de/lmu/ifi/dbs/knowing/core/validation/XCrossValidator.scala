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

  var confusionMatrix: Instances = _
  var classLabels: Array[String] = Array()

  def this() = this(null, 10, new Properties)
  
  override def customReceive = {
    case Results(instances) => build(instances)
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
    confusionMatrix = ResultsUtil.confusionMatrix(getClassLabels.toList)
    //TODO instantiate CrossValidator-actors
    val crossValidators = for (i <- 0 until folds; val actor = factory.getInstance.start) yield actor;
    debug(this, "Fold-Actors created!")
    for (j <- 0 until folds) {
      crossValidators(j) !! Register(self, None)
      crossValidators(j) !! Configure(configureProperties(validator_properties, j))
      crossValidators(j) ! Results(instances.trainCV(folds, j))
    }
    debug(this, "Fold-Actors configured and trained")

  }

  def result(result: Instances, query: Instance) {
	if(!result.equalHeaders(confusionMatrix))
	  warning (this, "Model ConfusionMatrix doesn't fit Result ConfusionMatrix")
	for(i <- 0 until confusionMatrix.numInstances) {
	  for(j <- 0 until confusionMatrix.numAttributes) {
	    val valResult = result get(i) value(j)
	    val valMatrix = confusionMatrix get(i) value(j)
	    val values = (valResult, valMatrix)
	    values match {
	      case (0, matrix) => //change nothing
	      case (result,0) => confusionMatrix get(i) setValue(j, result)
	      case (v1,v2) => confusionMatrix get(i) setValue(j, (v1+v2) / 2.0)
	    }
	  }
	}
	sendEvent(Results(confusionMatrix))
  }

  def configure(properties: Properties) = {
    debug (this, "configure with: " + properties)
    val factory = Util.getFactoryService(CrossValidatorFactory.id)
    factory match {
      case Some(f) => this.factory = f
      case None => throw new Exception("No Factory with " + CrossValidatorFactory.id + " found!")
    }
    val strFolds = properties.getProperty(CrossValidatorFactory.FOLDS, "10")
    folds = strFolds.toInt
    validator_properties = properties
  }

  private def configureProperties(properties: Properties, fold: Int): Properties = {
    val returns = new Properties
    returns.putAll(properties)
    returns.setProperty(CrossValidatorFactory.FOLD, fold.toString)
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