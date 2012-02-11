/*																*\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|	**
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,	**
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|	**
** 																**
** Knowing Framework											**
** Apache License - http://www.apache.org/licenses/				**
** LMU Munich - Database Systems Group							**
** http://www.dbs.ifi.lmu.de/									**
\*																*/
package de.lmu.ifi.dbs.knowing.weka.classifier

import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory._
import de.lmu.ifi.dbs.knowing.core.weka._
import de.lmu.ifi.dbs.knowing.core.weka.WekaClassifierFactory._
import java.util.Properties
import weka.classifiers.bayes.BayesNet
import WekaBayesNetFactory._

class WekaBayesNet extends WekaClassifier(new BayesNet) {

	override def configure(properties: Properties) = {
		val bayes = classifier.asInstanceOf[BayesNet]

		//TODO resolve BIF file. See #32
		//bayes.setBIFFile(resolvedFile)

		//TODO resolve EstimatorFactory and create one. See #32
		//bayes.setEstimator()

		//TODO resolve SearchAlgorith. See #32
		//bayes.setSearchAlgorithm()

		bayes.setUseADTree(true)

		val debug = properties.getProperty(DEBUG, "false")
		bayes.setDebug(debug.toBoolean)
	}

}

class WekaBayesNetFactory extends WekaClassifierFactory[WekaBayesNet, BayesNet](classOf[WekaBayesNet], classOf[BayesNet]) {

	override def createDefaultProperties: Properties = {
		val returns = new Properties
		returns.setProperty(DEBUG, "false")
		returns.setProperty(ADTREE, "false")
		returns
	}

	override def createPropertyValues: Map[String, Array[_ <: Any]] = {
		Map(ADTREE -> BOOLEAN_PROPERTY,
			DEBUG -> BOOLEAN_PROPERTY)
	}

	override def createPropertyDescription: Map[String, String] = {
		Map(ADTREE -> ADTREE_DESCR,
			SEARCH_ALGORITHM -> SEARCH_ALGORITHM_DESCRIPTION,
			ESTIMATOR -> ESTIMATOR_DESCR,
			BIFFILE -> BIFFILE_DESCR,
			DEBUG -> "Debug true/false")
	}

}

object WekaBayesNetFactory {

	val ADTREE = "ADTree"
	val ADTREE_DESCR = new StringBuilder()
		.append("When ADTree (the data structure for increasing speed on counts,")
		.append(" not to be confused with the classifier under the same name) is used")
		.append(" learning time goes down typically. However, because ADTrees are memory")
		.append(" intensive, memory problems may occur. Switching this option off makes")
		.append(" the structure learning algorithms slower, and run with less memory.")
		.append(" By default, ADTrees are used.")
		.toString

	val SEARCH_ALGORITHM_DESCRIPTION = "Select method used for searching network structures."

	val ESTIMATOR_DESCR = new StringBuilder()
		.append("Select Estimator algorithm for finding the conditional probability tables")
		.append(" of the Bayes Network.")
		.toString

	val BIFFILE = "biffile"
	val BIFFILE_DESCR = new StringBuilder()
		.append("Set the name of a file in BIF XML format. A Bayes network learned")
		.append(" from data can be compared with the Bayes network represented by the BIF file.")
		.append(" Statistics calculated are o.a. the number of missing and extra arcs.")
		.toString

}