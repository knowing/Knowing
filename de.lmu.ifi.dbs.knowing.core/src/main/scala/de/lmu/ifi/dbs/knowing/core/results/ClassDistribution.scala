package de.lmu.ifi.dbs.knowing.core.results

import java.util.{ ArrayList }
import weka.core.{ Instances, Attribute, DenseInstance }
import scala.collection.JavaConversions._

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

	val name = "class-distribution"

	def newInstances(): Instances = throw new UnsupportedOperationException("Cannot create class distribution without classes")

	/**
	 *
	 */
	def newInstances(classes: List[String], size: Int = 0): Instances = new Instances(name, attributes(classes), size)

	/**
	 *
	 */
	def newInstances(classes: List[String], distributions: List[Array[Double]]): Instances = {
		distributions.foldLeft(new ClassDistributionBuilder(classes,distributions.size))((builder, dist) => (builder + dist)).instances
	}

	/**
	 * Generates attributes for given classes input
	 * @param classes
	 */
	def attributes(classes: List[String]): ArrayList[Attribute] = {
		val attributes = new ArrayList[Attribute]
		for (clazz <- classes) {
			attributes.add(new Attribute(ATTRIBUTE_CLASS_PREFIX + clazz))
		}
		attributes
	}

	def apply(classes: List[String]): Instances = newInstances(classes)

}

/**
 *
 */
class ClassDistributionBuilder(classes: List[String], size: Int = 0) {

	val instances = ClassDistribution.newInstances(classes, size)

	/**
	 * Add distribution to classDistribution
	 */
	def +(distribution: Array[Double]): ClassDistributionBuilder = {
		if (instances.numAttributes != distribution.length)
			throw new IllegalArgumentException("ClassDistribution-Attributes length must be equal to distribution array. " + instances.numAttributes + " != " + distribution.length)
		instances.add(new DenseInstance(1, distribution))
		this
	}

}