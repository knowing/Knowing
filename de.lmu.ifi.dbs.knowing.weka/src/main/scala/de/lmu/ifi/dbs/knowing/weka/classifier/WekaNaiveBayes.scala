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
package de.lmu.ifi.dbs.knowing.weka.classifier

import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory._
import de.lmu.ifi.dbs.knowing.core.weka._
import de.lmu.ifi.dbs.knowing.core.weka.WekaClassifierFactory._
import java.util.Properties
import WekaNaiveBayesFactory._

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.04.2011
 */
class WekaNaiveBayes extends WekaClassifier(new weka.classifiers.bayes.NaiveBayes) {

	override def configure(properties: Properties) = {
		val bayes = classifier.asInstanceOf[weka.classifiers.bayes.NaiveBayes]

		val kernel = properties.getProperty(KERNEL_ESTIMATOR, "false")
		bayes.setUseKernelEstimator(kernel.toBoolean)

		val supervised = properties.getProperty(SUPERVISED_DISCRETIZATION, "false")
		bayes.setUseSupervisedDiscretization(supervised.toBoolean)

		val debug = properties.getProperty(DEBUG, "false")
		bayes.setDebug(debug.toBoolean)
	}
}

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.04.2011
 */
class WekaNaiveBayesFactory extends WekaClassifierFactory[WekaNaiveBayes, weka.classifiers.bayes.NaiveBayes](classOf[WekaNaiveBayes], classOf[weka.classifiers.bayes.NaiveBayes]) {

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

object WekaNaiveBayesFactory {
	val KERNEL_ESTIMATOR = "kernel-estimator"
	val SUPERVISED_DISCRETIZATION = "supervised-discretization"
}