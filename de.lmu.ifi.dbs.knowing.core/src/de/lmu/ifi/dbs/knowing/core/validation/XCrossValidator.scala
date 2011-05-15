package de.lmu.ifi.dbs.knowing.core.validation


import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.Util
import de.lmu.ifi.dbs.knowing.core.events._
import java.util.Properties
import weka.core.{ Instance, Instances }
import akka.event.EventHandler


class XCrossValidator extends TProcessor {

  var factory: TFactory = _
  var folds:Int = _
  var classifier_properties:Properties = _

  def build(instances: Instances) = {
    //TODO instantiate CrossValidator-actors
    val classifier = for (i <- 0 until folds; val actor = factory.getInstance.start) yield actor;
    EventHandler.debug(this,"Fold-Actors created!")
    for(j <- 0 until folds) {
      classifier(j) !! Configure(classifier_properties)
      classifier(j) ! Results(instances.trainCV(folds, j)) 
    }
    EventHandler.debug(this,"Fold-Actors configured and trained")
    
  }

  def configure(properties: Properties) = {
	  val factoryId = properties.getProperty(XCrossValidatorFactory.CLASSIFIER)
	  val factory = Util.getFactoryService(factoryId)
	  factory match {
	    case Some(f) => this.factory = f
	    case None => throw new Exception("No Factory with " + factoryId + " found!")
	  }
	  val strFolds = properties.getProperty(XCrossValidatorFactory.FOLDS, "10")
	  folds = strFolds.toInt
	  properties.remove(XCrossValidatorFactory.CLASSIFIER)
	  properties.remove(XCrossValidatorFactory.FOLDS)
	  classifier_properties = properties
  }
  
  def query(query: Instance):Instances = {null}

  def getClassLabels(): Array[String] = { null }

}

class XCrossValidatorFactory extends TFactory {

  val name: String = XCrossValidatorFactory.name
  val id: String = XCrossValidatorFactory.id

  def getInstance(): ActorRef = actorOf[XCrossValidator]

  def createDefaultProperties: Properties = {
    val props = new Properties();
    props.setProperty(XCrossValidatorFactory.CLASSIFIER, "")
    props.setProperty(XCrossValidatorFactory.FOLDS, "10")
    props
  }

  def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()
}

object XCrossValidatorFactory {

  val name: String = "XCrossValidator"
  val id: String = classOf[XCrossValidator].getName
  val CLASSIFIER = CrossValidatorFactory.CLASSIFIER
  val FOLDS = "folds"
}