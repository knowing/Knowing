package de.lmu.ifi.dbs.knowing.core.weka

import de.lmu.ifi.dbs.knowing.core.test.Tester
import java.util.Properties

import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.events.Results
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor

import weka.classifiers.Classifier
import weka.core.{ Instance, Instances }

import akka.actor.ActorRef
import akka.actor.Actor.actorOf

import scala.collection.JavaConversions._

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.04.2011
 * 
 */
class WekaClassifier(classifier: Classifier) extends TProcessor {

  private var classLabels: Array[String] = _
  private val name = getClass().getSimpleName;

  def build(instances: Instances) = {
    log debug ("Build internal model for " + name + " ...")
    val index = guessAndSetClassLabel(instances)
    index match {
      case -1 =>
        classLabels = Array()
        log warning ("No classLabel found in " + name)
      case x: Int => classLabels = classLables(instances.attribute(x))
    }
    classifier.buildClassifier(instances)
    log debug ("... build successfull for " + name)
  }

  def query(query: Instance) = {
    val distribution = classifier.distributionForInstance(query)
    val result = ResultsUtil.classAndProbabilityResult(getClassLabels.toList, distribution)
    self reply Results(result)
  }

  def getClassLabels(): Array[String] = classLabels
  
  def configure(properties:Properties) = {} //Override for special behaviour

}

/* =========================== */
/* ==== Processor Factory ==== */
/* =========================== */

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.04.2011
 */
class WekaClassifierFactory[T <: WekaClassifier, S <: Classifier](wrapper: Class[T], clazz: Class[S]) extends TFactory {

  val name: String = clazz.getSimpleName
  val id: String = clazz.getName

  def getInstance(): ActorRef = actorOf(wrapper)

  /* ======================= */
  /* ==== Configuration ==== */
  /* ======================= */

  def createDefaultProperties: Properties = new Properties

  def createPropertyValues: Map[String, Array[Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()

}

/* =========================== */
/* ==== WEKA Classifiers= ==== */
/* =========================== */

/**
 * @see {@link weka.classifiers.rules.OneR}
 */
class OneR extends WekaClassifier(new weka.classifiers.rules.OneR())

class OneRFactory extends WekaClassifierFactory[OneR, weka.classifiers.rules.OneR](classOf[OneR], classOf[weka.classifiers.rules.OneR]) 
