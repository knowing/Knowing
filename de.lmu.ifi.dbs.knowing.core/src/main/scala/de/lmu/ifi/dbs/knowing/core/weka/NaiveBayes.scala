package de.lmu.ifi.dbs.knowing.core.weka

import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.processing.{ TProcessor, TSender }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory._
import de.lmu.ifi.dbs.knowing.core.weka.WekaClassifierFactory._
import de.lmu.ifi.dbs.knowing.core.weka.NaiveBayesFactory._

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
class NaiveBayes extends WekaClassifier(new weka.classifiers.bayes.NaiveBayes) {

	override def configure(properties: Properties) = {
		val bayes = classifier.asInstanceOf[weka.classifiers.bayes.NaiveBayes]

		val kernel = properties.getProperty(KERNEL_ESTIMATOR, "false")
		val boolKernel = kernel.toBoolean
		bayes.setUseKernelEstimator(boolKernel)

		val supervised = properties.getProperty(SUPERVISED_DISCRETIZATION, "false")
		bayes.setUseSupervisedDiscretization(supervised.toBoolean)

		val debug = properties.getProperty(DEBUG, "false")
		bayes.setDebug(debug.toBoolean)
	}
}

/* =========================== */
/* ==== Processor Factory ==== */
/* =========================== */

object NaiveBayesFactory {
	val KERNEL_ESTIMATOR = "kernel-estimator"
	val SUPERVISED_DISCRETIZATION = "supervised-discretization"
}

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.04.2011
 */
class NaiveBayesFactory extends WekaClassifierFactory[NaiveBayes, weka.classifiers.bayes.NaiveBayes](classOf[NaiveBayes], classOf[weka.classifiers.bayes.NaiveBayes]) {

	override def createDefaultProperties: Properties = {
		val returns = new Properties
		returns.setProperty(KERNEL_ESTIMATOR, "false")
		returns.setProperty(SUPERVISED_DISCRETIZATION, "false")
		returns.setProperty(DEBUG, "false")
		returns
	}

	override def createPropertyValues: Map[String, Array[_ <: Any]] = {
		Map(KERNEL_ESTIMATOR -> BOOLEAN_PROPERTY,
			SUPERVISED_DISCRETIZATION -> BOOLEAN_PROPERTY,
			DEBUG -> BOOLEAN_PROPERTY)
	}

	override def createPropertyDescription: Map[String, String] = {
		Map(KERNEL_ESTIMATOR -> "?",
			SUPERVISED_DISCRETIZATION -> "?",
			DEBUG -> "Debug true/false")
	}
}
