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
package de.lmu.ifi.dbs.knowing.core.validation

import java.util.Properties
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.events.KnowingException
import de.lmu.ifi.dbs.knowing.core.results.{ConfusionMatrixResults,ClassDistributionResults}
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import weka.core.{ Attribute, Instances, Instance }
import ConfusionMatrix._

/**
 * Creates a ConfusionMatrix Instances object.
 * Input Instances must have been created with ResultsUtil.appendClassDistribution(..)
 *
 * PORTS:
 * <li>default - confusionMatrix </li>
 * <li>unclassified - all unclassified instances will be send to this port</li>
 *
 * @author Nepomuk Seiler
 * @version 0.3
 */
class ConfusionMatrix extends TProcessor {

	private var confusionMatrix: Instances = _
	private var unclassified: Instances = _

	private var instancesCount = Array[Int]()
	private var classLabels = Array[String]()

	//properties
	private var percentage = PERCENTAGE_ABSOLUTE

	def process(instances: Instances) = {
		case (_, _) =>
			init(instances)

			percentage match {
				case PERCENTAGE_DISTRIBUTION =>
					warning(this, "Using absolute confusion matrix. Relative not implemented yet")
					absoluteMatrix(instances)
					//distributedMatrix(instances)
				case PERCENTAGE_ABSOLUTE => absoluteMatrix(instances)
			}

			if (unclassified.size > 0)
				warning(this, "Unclassified: " + unclassified.size)

			sendResults(confusionMatrix)
			sendResults(unclassified, Some(UNCLASSIFIED_PORT))

			clean()
	}

	private def init(instances: Instances) {
		classLabels = ClassDistributionResults.extractClassLabels(instances).toArray
		confusionMatrix = ConfusionMatrixResults(classLabels.toList)
		unclassified = new Instances(instances, instances.size / 100) //1% unclassified
	}

	private def absoluteMatrix(instances: Instances) {
		val classOccurences = new Array[Double](classLabels.length)
		for (i <- 0 until instances.numInstances) {
			val inst = instances.get(i)
			val rowIndex = inst.classValue.toInt //actual class
			val colIndex = ClassDistributionResults.highestProbabilityIndex(instances, i)

			val row = confusionMatrix.get(rowIndex)
			val oldCount = row.value(colIndex)
			row.setValue(colIndex, oldCount + 1.0)
			classOccurences(rowIndex) += 1
		}

		for (rowIndex <- 0 until confusionMatrix.size) {
			val row = confusionMatrix.get(rowIndex)
			val occurences = classOccurences(rowIndex)
			for (colIndex <- 0 until confusionMatrix.numAttributes) {
				occurences match {
					case 0 => row.setValue(colIndex, 100.00)
					case x =>
						row.setValue(colIndex, (100 * row.value(colIndex)) / x)
				}
			}
		}
	}

	private def clean() {
		confusionMatrix = null
		unclassified = null
		instancesCount = Array[Int]()
		classLabels = Array[String]()
	}

	def query(query: Instances): Instances = throw new UnsupportedOperationException("query(..) on ConfusionMatrix is not defined")

	def configure(properties: Properties) = {
		percentage = properties.getProperty(PERCENTAGE, PERCENTAGE_ABSOLUTE)
	}

}

object ConfusionMatrix {
	val UNCLASSIFIED_PORT = "unclassified"

	val PERCENTAGE = "percentage"
	val PERCENTAGE_ABSOLUTE = "absolute"
	val PERCENTAGE_DISTRIBUTION = "distribution"
}

class ConfusionMatrixFactory extends ProcessorFactory(classOf[ConfusionMatrix]) {

}
