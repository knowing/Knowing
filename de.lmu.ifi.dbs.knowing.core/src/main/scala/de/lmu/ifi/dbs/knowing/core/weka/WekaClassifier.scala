/*                                                              *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
**                                                              **
** Knowing Framework                                            **
** Apache License - http://www.apache.org/licenses/             **
** LMU Munich - Database Systems Group                          **
** http://www.dbs.ifi.lmu.de/                                   **
\*                                                              */
package de.lmu.ifi.dbs.knowing.core.weka

import java.util.Properties
import java.io.{ InputStream, OutputStream }
import scala.collection.JavaConversions._
import de.lmu.ifi.dbs.knowing.core.factory.{TFactory,ProcessorFactory}
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.processing.{ TClassifier, TClassPropertyResolver, INodeProperties, ExecutionContext }
import de.lmu.ifi.dbs.knowing.core.japi.ILoggableProcessor
import de.lmu.ifi.dbs.knowing.core.results.ClassDistributionResultsBuilder
import akka.actor.{ Props, ActorRef, ActorSystem, ActorContext }
import weka.classifiers.Classifier
import weka.core.{ Instance, Instances }
import weka.core.SerializationHelper

/**
 *
 * Wraps the WEKA classifier interface.
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 21.04.2011
 *
 */
class WekaClassifier(var classifier: Classifier) extends TClassifier with TClassPropertyResolver {

	private var classLabels = Array[String]()
	private val name = getClass.getSimpleName

	def build(instances: Instances) {
		log.debug("Build internal model for " + name + " ...")
		guessAndCreateClassLabels(instances)
		classifier.buildClassifier(instances)
		isBuild = true
		log.debug("... build successfull for " + name)
	}

	def guessAndCreateClassLabels(instances: Instances) = guessAndSetClassLabel(instances) match {
		case -1 =>
			classLabels = Array()
			log.warning("No classLabel found in " + name)
		case x => classLabels = classLables(instances.attribute(x))
	}

	def query(query: Instances): Instances = {
		val builder = new ClassDistributionResultsBuilder(classLabels.toList)
		for (i <- 0 until query.numInstances) {
			val inst = query.get(i)
			builder + classifier.distributionForInstance(inst)
		}
		builder.instances
	}

	def getClassLabels(): Array[String] = classLabels

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
class WekaClassifierFactory[T <: WekaClassifier, S <: Classifier](wrapper: Class[T], clazz: Class[S]) extends ProcessorFactory(wrapper) {

	override val name: String = clazz.getSimpleName
	override val id: String = clazz.getName

	override def getInstance(context: ExecutionContext, factory: TFactory.ActorFactory): ActorRef = {
		classOf[ILoggableProcessor].isAssignableFrom(clazz) match {
			case false => factory.actorOf(Props(wrapper.newInstance), context.name)
			case true =>
				factory.actorOf(Props {
					val w = wrapper.newInstance
					w.classifier.asInstanceOf[ILoggableProcessor].setProcessor(w)
					w
				})
		}
	}
}

object WekaClassifierFactory {
	val DEBUG = INodeProperties.DEBUG
	val ESTIMATOR = "estimator"
	val SEARCH_ALGORITHM = "searchAlgorithm"
}

/* =========================== */
/* ==== WEKA Classifiers= ==== */
/* =========================== */

/**
 * @see {@link weka.classifiers.rules.OneR}
 */
class OneR extends WekaClassifier(new weka.classifiers.rules.OneR())

class OneRFactory extends WekaClassifierFactory[OneR, weka.classifiers.rules.OneR](classOf[OneR], classOf[weka.classifiers.rules.OneR]) 
