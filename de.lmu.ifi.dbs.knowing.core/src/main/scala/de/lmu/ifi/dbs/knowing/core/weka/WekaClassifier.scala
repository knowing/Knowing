package de.lmu.ifi.dbs.knowing.core.weka

import java.util.Properties
import java.io.{ InputStream, OutputStream }
import scala.collection.JavaConversions._
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.processing.TClassifier
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import weka.classifiers.Classifier
import weka.core.{ Instance, Instances }
import weka.core.SerializationHelper
import de.lmu.ifi.dbs.knowing.core.japi.ILoggableProcessor

/**
 *
 * Wraps the WEKA classifier interface.
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 21.04.2011
 *
 */
class WekaClassifier(var classifier: Classifier) extends TClassifier {

  private var classLabels = Array[String]()
  private val name = getClass.getSimpleName

  def build(instances: Instances) {
    debug(this, "Build internal model for " + name + " ...")
    guessAndCreateClassLabels(instances)
    classifier.buildClassifier(instances)
    debug(this, "... build successfull for " + name)
    processStoredQueries
  }

  def guessAndCreateClassLabels(instances: Instances) = guessAndSetClassLabel(instances) match {
    case -1 =>
      classLabels = Array()
      warning(this, "No classLabel found in " + name)
    case x => classLabels = classLables(instances.attribute(x))
  }

  def query(query: Instance): Instances = {
    val distribution = classifier.distributionForInstance(query)
    ResultsUtil.classAndProbabilityResult(getClassLabels.toList, distribution)
  }

  def getClassLabels(): Array[String] = classLabels

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

  def getInstance(): ActorRef = {
    classOf[ILoggableProcessor].isAssignableFrom(clazz) match {
      case false => actorOf(wrapper)
      case true =>
        actorOf {
          val w = wrapper.newInstance
          w.classifier.asInstanceOf[ILoggableProcessor].setProcessor(w)
          w
        }
    }
  }

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