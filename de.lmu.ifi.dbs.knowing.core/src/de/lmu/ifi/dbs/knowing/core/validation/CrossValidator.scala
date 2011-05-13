package de.lmu.ifi.dbs.knowing.core.validation


import akka.actor.ActorRef
import akka.actor.Actor.actorOf

import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.Util
import de.lmu.ifi.dbs.knowing.core.events._
import java.util.Properties

import weka.core.{ Instance, Instances }


class CrossValidator extends TProcessor {

  var factory: TFactory = _
  var folds:Int = _
  var classifier_properties:Properties = _

  def build(instances: Instances) = {
    val classifier = for (i <- 0 until folds; val actor = factory.getInstance.start) yield actor;
    log debug ("Fold-Actors created!")
    for(j <- 0 until folds) {
      classifier(j) !! Configure(classifier_properties)
      classifier(j) ! Results(instances.trainCV(folds, j)) 
    }
    log debug ("Fold-Actors configured and trained")
    
  }

  def configure(properties: Properties) = {
	  val factoryId = properties.getProperty(CrossValidatorFactory.CLASSIFIER)
	  val factory = Util.getFactoryService(factoryId)
	  factory match {
	    case Some(f) => this.factory = f
	    case None => throw new Exception("No Factory with " + factoryId + " found!")
	  }
	  val strFolds = properties.getProperty(CrossValidatorFactory.FOLDS, "10")
	  folds = strFolds.toInt
	  properties.remove(CrossValidatorFactory.CLASSIFIER)
	  properties.remove(CrossValidatorFactory.FOLDS)
	  classifier_properties = properties
  }
  
  def query(query: Instance):Instances = {null}

  def getClassLabels(): Array[String] = { null }

}

class CrossValidatorFactory extends TFactory {

  val name: String = CrossValidatorFactory.name
  val id: String = CrossValidatorFactory.id

  def getInstance(): ActorRef = actorOf[CrossValidator]

  def createDefaultProperties: Properties = {
    val props = new Properties();
    props.setProperty(CrossValidatorFactory.CLASSIFIER, "")
    props
  }

  def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()
}

object CrossValidatorFactory {

  val name: String = "CrossValidator"
  val id: String = "de.lmu.ifi.dbs.knowing.core.validation.CrossValidator"

  val CLASSIFIER = "classifier"
  val FOLDS = "folds"
}