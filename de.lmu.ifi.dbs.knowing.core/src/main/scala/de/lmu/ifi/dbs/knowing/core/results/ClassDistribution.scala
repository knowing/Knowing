package de.lmu.ifi.dbs.knowing.core.results

import java.util.{ ArrayList }
import weka.core.{ Instances, Attribute, DenseInstance }
import scala.collection.JavaConversions._
import weka.core.Utils

/**
 * <p>Represents a class distribution format.</p>
 *
 * <p>Format for given input classes = (A,B,C)
 *
 * classA | classB | classC
 * 0.3    | 0.3    | 0.4
 * 0.1    | 0.8    | 0.1
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
object ClassDistribution extends ResultsType {

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
		distributions.foldLeft(new ClassDistributionBuilder(classes, distributions.size))((builder, dist) => (builder + dist)).instances
	}

	/**
	 * Generates attributes for given classes input
	 * @param classes
	 */
	def attributes(classes: List[String]): ArrayList[Attribute] = {
		val attributes = new ArrayList[Attribute]
		attributes.add(new Attribute(ATTRIBUTE_CLASS, classes))
		for (clazz <- classes) {
			attributes.add(new Attribute(ATTRIBUTE_CLASS_PREFIX + clazz))
		}
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
		//-1, because first attribute is class attribute
		attrIndex-1
	}

	/**
	 * @param the classDistribution
	 * @return an array with all attribute names starting with ATTRIBUTE_CLASS_PREFIX, where ATTRIBUTE_CLASS_PREFIX is removed
	 */
	def extractClassLabels(classDistribution: Instances): Array[String] = {
		val classes = for (
			i <- 0 until classDistribution.numAttributes if (classDistribution.attribute(i).name.startsWith(ATTRIBUTE_CLASS_PREFIX)
				&& !classDistribution.attribute(i).name.equals(ATTRIBUTE_CLASS))
		) yield classDistribution.attribute(i).name.substring(ATTRIBUTE_CLASS_PREFIX.length)
		classes.toArray
	}

}

/**
 *
 */
class ClassDistributionBuilder(classes: List[String], size: Int = 0) {

	val instances = ClassDistribution.newInstances(classes, size)

	/**
	 * Add distribution to classDistribution
	 * Class will be set to unkown
	 */
	def +(distribution: Array[Double]): ClassDistributionBuilder = this.+(distribution, Utils.missingValue)

	/**
	 * Add distribution to classDistribution
	 * Class will be set to unkown
	 */
	def +(distribution: Array[Double], classValueIndex: Double): ClassDistributionBuilder = {
		if (instances.numAttributes != distribution.length + 1)
			throw new IllegalArgumentException("ClassDistribution-Attributes length must be equal to distribution array. " + instances.numAttributes + " != " + distribution.length)
		val newDist = new Array[Double](distribution.length + 1)
		newDist(0) = classValueIndex // Missing class
		for (i <- 1 until newDist.length)
			newDist(i) = distribution(i - 1)

		instances.add(new DenseInstance(1, newDist))
		this
	}

}