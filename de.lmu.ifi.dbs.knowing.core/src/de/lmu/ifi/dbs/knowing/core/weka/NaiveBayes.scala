package de.lmu.ifi.dbs.knowing.core.weka

import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.processing.{ TProcessor , TSender }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory._

import weka.core.{ Instance, Instances }

import akka.actor.ActorRef
import akka.actor.Actor.actorOf

import java.util.Properties

import scala.collection.JavaConversions._

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.04.2011
 */
class NaiveBayes extends TProcessor {

  lazy val naiveBayes = new weka.classifiers.bayes.NaiveBayes()

  def build(instances: Instances) =  {
    log debug("Build classifier...")
    guessAndSetClassLabel(instances)
    naiveBayes buildClassifier(instances)
    log debug("... succesfull")
    sendEvent(Ready())
  }

  def query(query: Instance) = {
	  val distribution = naiveBayes distributionForInstance(query)
	  val result = ResultsUtil.classAndProbabilityResult(getClassLabels.toList, distribution)
	  self reply Results(result)
  }

  def getClassLabels: Array[String] =  Array()
  
  def configure(properties:Properties) = {}
}

/* =========================== */
/* ==== Processor Factory ==== */
/* =========================== */

object NaiveBayesFactory {
  val name: String = "Naive Bayes"
  val id: String = "weka.classifiers.bayes.NaiveBayes"
}

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.04.2011
 */
class NaiveBayesFactory extends TFactory {
  val name: String = NaiveBayesFactory.name
  val id: String = NaiveBayesFactory.id

  def getInstance: ActorRef = actorOf[NaiveBayes]

  def createDefaultProperties: Properties = {
    val returns = new Properties
    returns
  }

  def createPropertyValues: Map[String, Array[Any]] = {
    Map()
  }

  def createPropertyDescription: Map[String, String] = {
    Map()
  }
}
