package de.lmu.ifi.dbs.knowing.core.weka

import java.util.Properties
import scala.collection.JavaConversions._
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.processing.TSerializable
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import weka.classifiers.Classifier
import weka.core.{ Instance, Instances }
import weka.core.SerializationHelper


/**
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 21.04.2011
 *
 */
class WekaClassifier(protected var classifier: Classifier) extends TProcessor with TSerializable {

  private var classLabels: Array[String] = _
  private val name = getClass().getSimpleName;
  
  override def start {
    inputStream match {
      case None => //no model found
      case Some(in) =>
        debug(this, "Deserialize stored classifier")
        classifier = SerializationHelper.read(in).asInstanceOf[Classifier]
        isBuild = true
    }
  }
  
  override def postStop {
    outputStream match {
      case None =>
      case Some(out) => 
        debug(this, "Serialize classifier")
        SerializationHelper.write(out, classifier)
    }
  }

  def build(instances: Instances) {
    debug(this, "Build internal model for " + name + " ...")
    val index = guessAndSetClassLabel(instances)
    index match {
      case -1 =>
        classLabels = Array()
        warning(this, "No classLabel found in " + name)
      case x => classLabels = classLables(instances.attribute(x))
    }
    classifier.buildClassifier(instances)
    debug(this, "... build successfull for " + name)
    processStoredQueries
  }

  def query(query: Instance): Instances = {
    val distribution = classifier.distributionForInstance(query)

    val distString = for (i <- 0 until distribution.length) yield distribution(i).toString
//    debug(this, "Classified with: " + distString + " # ClassValue: " + query.classValue)
    ResultsUtil.classAndProbabilityResult(getClassLabels.toList, distribution)
  }

  def getClassLabels(): Array[String] = classLabels

  def configure(properties: Properties) {} //Override for special behaviour

  def result(result: Instances, query: Instance) {} //Override for special behaviour

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

  def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()

}

object WekaClassifierFactory {
  val DEBUG = INodeProperties.DEBUG
}

/* =========================== */
/* ==== WEKA Classifiers= ==== */
/* =========================== */

/**
 * @see {@link weka.classifiers.rules.OneR}
 */
class OneR extends WekaClassifier(new weka.classifiers.rules.OneR())

class OneRFactory extends WekaClassifierFactory[OneR, weka.classifiers.rules.OneR](classOf[OneR], classOf[weka.classifiers.rules.OneR]) 
