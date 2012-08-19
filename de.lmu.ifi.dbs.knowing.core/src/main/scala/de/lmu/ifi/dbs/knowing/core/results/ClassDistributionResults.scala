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
package de.lmu.ifi.dbs.knowing.core.results

import java.util.{ ArrayList }
import weka.core.{ Instances, Attribute, DenseInstance, Utils }
import scala.collection.JavaConversions._
import de.lmu.ifi.dbs.knowing.core.exceptions.KnowingException
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor.guessAndSetClassLabel

/**
 * <p>Represents a class distribution format.</p>
 *
 * <p>Format for given input classes = (A,B,C)
 *
 * classA | classB | classC | class
 * 0.3    | 0.3    | 0.4    | C
 * 0.1    | 0.8    | 0.1    | B
 *
 * </p>
 * <p>Example:
 *
 * <code>
 * /* Simple to create*/
 * val classDist = newInstances(List("A","B","C"))
 *
 * /* Or use the builder */
 * val builder = new ClassDistributionBuilder(List("A","B","C"))
 * builder + (Array(0.3, 0.3, 0.4))
 *         + (Array(0.1, 0.8, 0.1))
 * </code>
 * </p>
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
object ClassDistributionResults extends ResultsType {

	val ATTRIBUTE_CLASS_PREFIX = ResultsType.ATTRIBUTE_CLASS_PREFIX
	val ATTRIBUTE_CLASS = ResultsType.ATTRIBUTE_CLASS

	val name = "class-distribution"

	def newInstances(): Instances = throw new UnsupportedOperationException("Cannot create class distribution without classes")

	/**
	 *
	 */
	def newInstances(classes: List[String], size: Int = 0): Instances = {
		val instances = new Instances(name, attributes(classes), size)
		instances.setClass(instances.attribute(ATTRIBUTE_CLASS))
		instances
	}

	/**
	 *
	 */
	def newInstances(classes: List[String], distributions: List[Array[Double]]): Instances = {
		distributions.foldLeft(new ClassDistributionResultsBuilder(classes, distributions.size))((builder, dist) => (builder + dist)).instances
	}

	/**
	 * Generates attributes for given classes input.
	 * Ordering is preserved. Last attribute is the (actual)
	 * class attribute.
	 *
	 * @param classes
	 */
	def attributes(classes: List[String]): ArrayList[Attribute] = {
		val attributes = new ArrayList[Attribute]
		for (clazz <- classes) {
			attributes.add(new Attribute(ATTRIBUTE_CLASS_PREFIX + clazz))
		}
		attributes.add(new Attribute(ATTRIBUTE_CLASS, classes))
		attributes
	}

	def apply(classes: List[String]): Instances = newInstances(classes)

	/*===================================== */
	/*========== Utility Methods ===========*/
	/*===================================== */

	/**
	 * Returns the attribute index of the Instance with the
	 * highest value. If two values are equal, the first will
	 * be chosen.
	 *
	 * @param classDistribution - the distribution
	 * @param index - which element of the classDistribution should be used
	 * @return the attribute index or -1
	 */
	def highestProbabilityIndex(classDistribution: Instances, index: Int): Int = {
		val inst = classDistribution.get(index)
		var attrIndex = -1
		var probability = 0.0
		for (
			i <- 0 until classDistribution.numAttributes if (classDistribution.attribute(i).name.startsWith(ATTRIBUTE_CLASS_PREFIX))
				&& !classDistribution.attribute(i).name.equals(ATTRIBUTE_CLASS)
		) {
			probability = inst.value(i) match {
				case p if p > probability =>
					attrIndex = i
					p
				case p if p <= probability => probability
			}
		}
		attrIndex
	}

	/**
	 * @param the classDistribution
	 * @return an array with all attribute names starting with ATTRIBUTE_CLASS_PREFIX, where ATTRIBUTE_CLASS_PREFIX is removed
	 */
	def extractClassLabels(classDistribution: Instances): List[String] = findClassDistributionAttributes(classDistribution) map {
		attr => attr.name.substring(ATTRIBUTE_CLASS_PREFIX.length)
	}

	/**
	 * Finds all class distribution values and returns a map
	 * where the raw class name is mapped to the containing attribute.
	 *
	 *
	 * @param distribution
	 * @return Map[String, Instances] == class name -> attribute
	 */
	def findClassDistributionAttributesAsMap(distribution: Instances): Map[String, Attribute] = {
		findClassDistributionAttributes(distribution) map (attr => (attr.name.substring(ATTRIBUTE_CLASS.length) -> attr)) toMap
	}

	/**
	 * Find all attributes indicating the class distribution.
	 *
	 * @param distribution
	 * @return attributes starting with "class" and not equally class
	 */
	def findClassDistributionAttributes(classDistribution: Instances): List[Attribute] = {
		val attributes = for (
			i <- 0 until classDistribution.numAttributes if (classDistribution.attribute(i).name.startsWith(ATTRIBUTE_CLASS_PREFIX)
				&& !classDistribution.attribute(i).name.equals(ATTRIBUTE_CLASS))
		) yield classDistribution.attribute(i)
		attributes.toList
	}

	/**
	 * Appends class+<classlabel> attributes with distribution
	 * to the given query Instances parameter.
	 *
	 * @param query - to which the results should be appended
	 * @param distribution - appended to query instances
	 * @return new Instances object with (query+distribution)
	 */
	def appendClassDistribution(query: Instances, distribution: Instances, setClass: Boolean = true): Instances = {
		if (query.numInstances != distribution.numInstances)
			throw new KnowingException("Query Instances and distribution Instances have different number of instances: "
				+ query.numInstances + " != " + distribution.numInstances, null)

		val returns = new Instances(query, 0)
		val classIndex = guessAndSetClassLabel(returns) match {
			case -1 => throw new KnowingException("No class index found", null)
			case i => i
		}

		val labels = returns.classAttribute.enumerateValues.toList
		labels foreach (l => returns.insertAttributeAt(new Attribute(ATTRIBUTE_CLASS + l), returns.numAttributes))

		for (i <- 0 until query.numInstances) {
			val numAttr = query.numAttributes
			val inst = new DenseInstance(returns.numAttributes)

			//Copy values
			for (j <- 0 until numAttr)
				inst.setValue(j, query.get(i).value(j))

			//Fill in distribution
			for (j <- numAttr until (numAttr + labels.size))
				inst.setValue(j, distribution.get(i).value(j - numAttr))

			returns.add(inst)
			if (setClass) {
				val clazz = highestProbabilityIndex(distribution, i)
				returns.lastInstance.setClassValue(clazz)
			}
		}

		returns
	}

}

/**
 *
 */
class ClassDistributionResultsBuilder(classes: List[String], size: Int = 0) {

	val instances = ClassDistributionResults.newInstances(classes, size)

	/**
	 * Add distribution to classDistribution
	 * Class will be set to unkown
	 */
	def +(distribution: Array[Double]): ClassDistributionResultsBuilder = this.+(distribution, Utils.missingValue)

	/**
	 * Add distribution to classDistribution
	 * Class will be set to unkown
	 */
	def +(distribution: Array[Double], classValueIndex: Double): ClassDistributionResultsBuilder = {
		if (instances.numAttributes != distribution.length + 1)
			throw new IllegalArgumentException("ClassDistribution-Attributes length must be equal to distribution array. " + instances.numAttributes + " != " + distribution.length)
		val newDist = new Array[Double](distribution.length + 1)
		for (i <- 0 until distribution.length)
			newDist(i) = distribution(i)

		newDist(newDist.length - 1) = classValueIndex // Missing class
		instances.add(new DenseInstance(1, newDist))
		this
	}

}