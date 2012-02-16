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
package de.lmu.ifi.dbs.knowing.core.validation

import java.util.Properties
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.events.KnowingException
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.{ confusionMatrix => createMatrix }
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil._
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
 * @version 0.1
 */
class ConfusionMatrix extends TProcessor {

	private var confusionMatrix: Instances = _
	private var unclassified: Instances = _

	private var instancesCount = Array[Int]()
	private var classLabels = Array[String]()

	//properties
	private var percentage = PERCENTAGE_ABSOLUTE

	def build(instances: Instances) {
		init(instances)
		ensureOrder(instances)

		percentage match {
			case PERCENTAGE_DISTRIBUTION => distributedMatrix(instances)
			case PERCENTAGE_ABSOLUTE => absoluteMatrix(instances)
		}

		merge()

		if (unclassified.size > 0)
			warning(this, "Unclassified: " + unclassified.size)

		sendResults(confusionMatrix)
		sendResults(unclassified, Some(UNCLASSIFIED_PORT))

		clean()
	}

	private def init(instances: Instances) {
		val index = guessAndSetClassLabel(instances)
		index match {
			case -1 =>
				classLabels = Array()
				warning(this, "No classLabel found in " + instances.relationName)
			case i =>
				classLabels = classLables(instances.attribute(i))
				instancesCount = new Array(classLabels.size)
		}
		confusionMatrix = createMatrix(classLabels.toList)
		unclassified = new Instances(instances, instances.size / 100) //1% unclassified
	}

	private def ensureOrder(instances: Instances) {
		val attributes = findClassDistributionAttributes(instances)
		for (i <- 0 until attributes.length) {
			val attr = attributes(i).name.substring(ATTRIBUTE_CLASS.length)
			val clazz = classLabels(i)
			if (!attr.equals(clazz))
				throwException(new KnowingException("ConfusionMatrix. ClassLabel order is wrong."), attr + " != " + clazz)
		}
	}

	/**
	 * Calculates a confusionMatrix with absolute percentage values.
	 * This means only the highest probability of a class is counted as 1
	 * and all others count as 0.
	 */
	private def absoluteMatrix(instances: Instances) {
		//Create confusion matrix
		val classIndex = instances.classIndex
		val enum = instances.enumerateInstances
		while (enum.hasMoreElements) {
			val inst = enum.nextElement.asInstanceOf[Instance]
			//distribution == ResultsUtil.classDistribution
			val clazz = highestProbability(inst)
			val col = inst.value(classIndex).toInt
			highestProbability(inst) match {
				case (-1, _) =>
					unclassified.add(inst)
				case (prob, clazz) =>
					//This should be solved via a map -> only for non-distributed
					for (row <- 0 until classLabels.length if classLabels(row).equals(clazz)) {
						val entry = confusionMatrix.instance(row)
						val old_value = entry.value(col)
						instancesCount(col) = instancesCount(col) + 1
						entry.setValue(col, old_value + 1)
					}
			}
		}
	}

	private def distributedMatrix(instances: Instances) {
		//Create confusion matrix
		val classIndex = instances.classIndex
		val attributes = findClassDistributionAttributes(instances)
		val enum = instances.enumerateInstances
		while (enum.hasMoreElements) {
			val inst = enum.nextElement.asInstanceOf[Instance]
			val col = inst.value(classIndex).toInt
			for (row <- 0 until classLabels.length) {
				//class probability
				val attr = attributes(row)
				val prob = inst.value(attr)

				//confusion matrix row
				val entry = confusionMatrix.instance(row)
				val oldValue = entry.value(col)
				entry.setValue(col, oldValue + prob)

			}
			instancesCount(col) = instancesCount(col) + 1
		}
	}

	private def merge() {
		val rows, cols = classLabels.size
		for (row <- 0 until rows) {
			val entry = confusionMatrix.instance(row)
			for (col <- 0 until cols) {
				val value = entry.value(col)
				//Avoid division with zero
				val average = instancesCount(col) match {
					case 0 => 1
					case x => x
				}
				//percent
				entry.setValue(col, (value / average) * 100)
			}
		}
	}

	private def clean() {
		confusionMatrix = null
		unclassified = null
		instancesCount = Array[Int]()
		classLabels = Array[String]()
	}

	def query(query: Instance): Instances = throw new UnsupportedOperationException("query(..) on ConfusionMatrix is not defined")

	def result(result: Instances, query: Instance) = throw new UnsupportedOperationException("result(..) on ConfusionMatrix is not defined")

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
