package de.lmu.ifi.dbs.knowing.core.results

import java.util.{ ArrayList, Arrays, Collections, List => JList, Properties, Map => JMap }
import weka.core.{ Attribute, ProtectedProperties, Instances, DenseInstance }
import scala.collection.JavaConversions._

/**
 *
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 12.03.2012
 */
object ClassAndProbability extends ResultsType {

	val name = "classAndProbability"

	val ATTRIBUTE_CLASS = ResultsType.ATTRIBUTE_CLASS
	val ATTRIBUTE_PROBABILITY = ResultsType.ATTRIBUTE_PROBABILITY

	/**
	 * <p>
	 * <li>relation name: classAndProbability</li>
	 * <li>attributes: {@link #ATTRIBUTE_CLASS}, {@link #ATTRIBUTE_PROBABILITY}</li>
	 * </p>
	 * 
	 * No class attribute is set. Nominal attribute "class" has no values.
	 *
	 * @return {@link Instances} with {@link Attribute}s: "class" and "probability"
	 */
	def newInstances(): Instances = {
		val attributes = new ArrayList[Attribute]

		val classAttribute = new Attribute(ATTRIBUTE_CLASS, Nil)
		val probaAttribute = new Attribute(ATTRIBUTE_PROBABILITY)
		attributes.add(classAttribute)
		attributes.add(probaAttribute)

		new Instances(name, attributes, 0)
	}

	/**
	 * <p>
	 * <li>relation name: classAndProbability</li>
	 * <li>attributes: {@link #ATTRIBUTE_CLASS}, {@link #ATTRIBUTE_PROBABILITY}</li>
	 * </p>
	 * 
	 * ClassAttribute is set.
	 *
	 * @param labels
	 * @return {@link Instances} with {@link Attribute}s: "class" and "probability"
	 */
	def newInstances(labels: List[String]): Instances = {
		val attributes = new ArrayList[Attribute]

		val classAttribute = new Attribute(ATTRIBUTE_CLASS, labels)
		val probaAttribute = new Attribute(ATTRIBUTE_PROBABILITY)
		attributes.add(classAttribute)
		attributes.add(probaAttribute)

		val returns = new Instances(name, attributes, 0)
		returns.setClass(classAttribute)
		returns
	}
	
	def apply(labels: List[String]): Instances = newInstances(labels)

	/**
	 * <p>
	 * <li>relation name: {@link #NAME_CLASS_AND_PROBABILITY}</li>
	 * <li>attributes: {@link #ATTRIBUTE_CLASS}, {@link #ATTRIBUTE_PROBABILITY}</li>
	 * </p>
	 *
	 * @param labels
	 * @return {@link Instances} with {@link Attribute}s: "class" and "probability"
	 */
	def newInstances(labels: JList[String]): Instances = newInstances(labels.toList)

	/**
	 * <p>Adds [code]distribution.length[/code] instances to the dataset.<p>
	 * <p>The lables list must have the same ordering as the distribution array</p>
	 *
	 * @param labels
	 * @param distribution
	 * @return
	 */
	def newInstances(labels: List[String], distribution: Array[Double]): Instances = {
		val returns = newInstances(labels)
		if (distribution.length > returns.numClasses)
			return returns

		val classAttribute = returns.attribute(ATTRIBUTE_CLASS)
		val probaAttribute = returns.attribute(ATTRIBUTE_PROBABILITY)
		for (i <- 0 until distribution.length) {
			val instance = new DenseInstance(2)
			//      instance.setValue(classAttribute, classAttribute.value(i))
			instance.setValue(probaAttribute, distribution(i))
			returns.add(i, instance)
			returns.lastInstance.setClassValue(classAttribute.value(i))
		}
		//Add 0.0 probability to non existing classLabels in distribution
		for (i <- distribution.length until (labels.size - distribution.length)) {
			val instance = new DenseInstance(2)
			instance.setValue(classAttribute, classAttribute.value(i))
			instance.setValue(probaAttribute, 0.0)
			returns.add(i, instance)
		}
		returns
	}
	
	def apply(labels: List[String], distribution: Array[Double]): Instances = newInstances(labels, distribution)

	/**
	 * <p>Adds [code]distribution.length[/code] instances to the dataset.<p>
	 * <p>The lables list must have the same ordering as the distribution array</p>
	 *
	 * @param labels
	 * @param distribution
	 * @return
	 */
	def newInstances(labels: JList[String], distribution: Array[Double]): Instances = newInstances(labels.toList)

}