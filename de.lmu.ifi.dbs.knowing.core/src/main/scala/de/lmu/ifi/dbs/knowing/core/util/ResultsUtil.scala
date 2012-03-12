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
package de.lmu.ifi.dbs.knowing.core.util

import java.util.{ ArrayList, Arrays, Collections, List => JList, Properties, Map => JMap }
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import weka.core.{ Attribute, DenseInstance, Instances, Instance, ProtectedProperties, WekaException }
import weka.core.SparseInstance
import de.lmu.ifi.dbs.knowing.core.processing.ImmutableInstances
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor

/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 24.04.2011
 *
 */
object ResultsUtil {

	val ATTRIBUTE_DUMMY = "dummy"
	val ATTRIBUTE_CLASS = "class"
	val ATTRIBUTE_CLASS_DISTRIBUTION = "class_distribution"
	val ATTRIBUTE_PROBABILITY = "probability"
	val ATTRIBUTE_TIMESTAMP = "timestamp"
	val ATTRIBUTE_VALUE = "y"
	val ATTRIBUTE_FROM = "from"
	val ATTRIBUTE_TO = "to"
	val ATTRIBUTE_SOURCE = "source"

	val NAME_EMPTY = "empty"
	val NAME_CLASS_ONLY = "class_only"
	val NAME_CLASS_AND_PROBABILITY = "class_and_probability"
	val NAME_CROSS_VALIDATION = "cross_validation"
	val NAME_CLASS_DISTRIBUTION = "class_distribution"
	val NAME_TIME_INTERVAL = "time_interval"
	val NAME_TIME_SERIES = "time_series"

	val NOT_CLASSIFIED = "not_classified";

	val META_ATTRIBUTE_NAME = "name"

	val DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss:SSS"

	val ORIGINAL_INSTANCES = "original"

	val UNKOWN_SOURCE = "unkown"


	/* ======================================================= */
	/* ======================== Utils ======================== */
	/* ======================================================= */

	/**
	 *  <p>Checks the dataset for class attribute in this order
	 *  <li> {@link Instances#classIndex()} -] if ]= 0 returns index</li>
	 *  <li> returns index of the attribute named "class" if exists</li>
	 *  <li> returns index of the first nominal attribute</li>
	 *  </p>
	 *
	 * @param dataset
	 * @return class attribute index or -1
	 */
	def guessClassIndex(dataset: Instances): Int = {
		val classIndex = dataset.classIndex();
		if (classIndex >= 0)
			return classIndex;

		val classAttribute = dataset.attribute("class");
		if (classAttribute != null)
			return classAttribute.index();

		//If no attribute named class was found, take the first nominal
		val attributes = dataset.enumerateAttributes()
		while (attributes.hasMoreElements) {
			val attribute = attributes.nextElement.asInstanceOf[Attribute]
			if (attribute.isNominal())
				return attribute.index()
		}
		-1;
	}

	/**
	 *
	 * @param dataset
	 * @return list with all numeric attributes
	 */
	def findNumericAttributes(dataset: Instances): JList[Attribute] = {
		val returns = new ArrayList[Attribute]
		val attributes = dataset.enumerateAttributes
		while (attributes.hasMoreElements) {
			val attribute = attributes.nextElement.asInstanceOf[Attribute]
			if (attribute.isNumeric)
				returns.add(attribute)
		}
		returns
	}

	/**
	 * <p>Converts a ImmutableInstances object into an mutable Instances object</p>
	 *
	 * @param ImmutableInstances
	 * @return Instances object
	 */
	def convertToMutable(instances: ImmutableInstances): Instances = appendInstances(new Instances(instances, 0), instances)

	@throws(classOf[WekaException])
	def appendInstancesPartial(header: Instances, append: Instances, numInstances: Int): Instances = {
		if (!header.equalHeaders(append))
			throw new WekaException("Instances headers are not equal")
		val ret = new Instances(header, numInstances)

		for (i <- 0 until header.numInstances if (i < numInstances)) ret.add(header(i))
		for (i <- 0 until append.numInstances if (i + header.numInstances < numInstances)) ret.add(append(i))

		ret
	}

	/**
	 * Just appends one 'append' to 'first' without changing attributes.
	 *
	 * @throws WekaException - if headers are not equal
	 * @returns Instances - new instances object with first -> append added
	 */
	@throws(classOf[WekaException])
	def appendInstances(header: Instances, append: Instances): Instances = {
		if (!header.equalHeaders(append))
			throw new WekaException("Instances headers are not equal")
		val ret = new Instances(header, header.numInstances + append.numInstances)
		val firstEnum = header.enumerateInstances
		while (firstEnum.hasMoreElements) ret.add(firstEnum.nextElement.asInstanceOf[Instance])
		val appendEnum = append.enumerateInstances
		while (appendEnum.hasMoreElements) ret.add(appendEnum.nextElement.asInstanceOf[Instance])
		ret
	}

	/**
	 * This method merges a list of instances into a given header. The header is normally empty.
	 */
	def appendInstances(header: Instances, datasets: List[Instances]): Instances = appendInstances(header, datasets, identity)

	/**
	 * This method merges a list of instance into a given header and manipulates every instance before adding.
	 */
	def appendInstances(header: Instances, datasets: List[Instances], f: Instances => Instances): Instances = {
		datasets.foldLeft(header)((result, inst) => appendInstances(result, f(inst)))
	}

	/**
	 * This method merges a list of instance into a given header and manipulates every instance before adding.
	 * The first tupel value can be used to give specific merge details for each instance.
	 */
	def appendInstancesTupel[A](header: Instances, datasets: List[(A, Instances)], f: (A, Instances) => Instances): Instances = {
		datasets.foldLeft(header)((result, inst) => appendInstances(result, f(inst._1, inst._2)))
	}

	/**
	 * <p>Splits a instances object with the given attribute into a map of source -> Instances
	 * If 'attribute' is not defined, the method returns ORIGINAL_INSTANCES -> instances</p>
	 *
	 * <p>Note: If instances is of type ImmutableInstances it will be converted
	 * into a Instances object so it can be edited</p>
	 *
	 *
	 * @param instances - Instances to split
	 * @param attribute - name of the attribute, the split should be performed on
	 * @returns source -> Instances
	 *
	 */
	def splitInstanceByAttribute(instances: Instances, attribute: String, removeAttr: Boolean = true): Map[String, Instances] = {
		(instances.attribute(attribute), instances) match {
			case (null, inst: ImmutableInstances) => Map(ORIGINAL_INSTANCES -> convertToMutable(inst))
			case (null, inst) => Map(ORIGINAL_INSTANCES -> inst)
			case (splitAttr, inst) =>
				val classMap = instances.groupBy(inst => splitAttr.value(inst.value(splitAttr) toInt))
				classMap map {
					case (clazz, list) =>
						val ret = new Instances(instances, list.length)
						list foreach (ret.add(_))
						if (removeAttr) ret.deleteAttributeAt(splitAttr.index)
						(clazz, ret)
				}
		}

	}

	/**
	 * @see splitInstanceByAttribute
	 */
	def splitInstanceByAttributeJava(instances: Instances, attribute: String, removeAttr: Boolean = true): JMap[String, Instances] = asMap(splitInstanceByAttribute(instances, attribute, removeAttr))

	/**
	 * <p>Splits a instances object with SOURCE_ATTRIBUTE into a map of source -> Instances
	 * If no SOURCE_ATTRIBUTE is defined, the method returns ORIGINAL_INSTANCES -> instances</p>
	 *
	 * @param instances - Instances to split
	 * @returns source -> Instances
	 *
	 */
	def splitInstanceBySource(instances: Instances, removeAttr: Boolean = true): Map[String, Instances] = splitInstanceByAttribute(instances, ATTRIBUTE_SOURCE, removeAttr)

	/**
	 * @see splitInstanceBySource
	 */
	def splitInstanceBySourceJava(instances: Instances, removeAttr: Boolean = true): JMap[String, Instances] = splitInstanceByAttributeJava(instances, ATTRIBUTE_SOURCE, removeAttr)

	/**
	 * <p>returns the highest probability and the class name</p>
	 *
	 * @param distribution - must have been created with @link{appendClassDistribution(header, inst, setClass)}
	 * @return (probability, class) or (0, NOT_CLASSIFIED)
	 */
	def highestProbability(distribution: Instance): (Double, String) = {
		val classAttr = distribution.enumerateAttributes.toList filter {
			case a: Attribute => a.name.size > 5 && a.name.startsWith("class")
		}

		classAttr.foldLeft((-1.0, NOT_CLASSIFIED)) {
			case ((max, clazz), a: Attribute) =>
				val value = distribution.value(a)
				if (value > max) (value, a.name.substring(5))
				else (max, clazz)
		}
	}

	/**
	 * @param distribution - must have been created with @link{classAndProbabilityResult(classes, distribution)}
	 * @return (probability, class) or (0, NOT_CLASSIFIED)
	 */
	def highestProbability(distribution: Instances): (Double, String) = {
		val classAttr = distribution.attribute(ATTRIBUTE_CLASS)
		val probAttr = distribution.attribute(ATTRIBUTE_PROBABILITY)
		var ret = (0.0, NOT_CLASSIFIED)
		for (i <- 0 until distribution.length) {
			val inst = distribution(i)
			inst.value(probAttr) match {
				case probability if probability > ret._1 =>
					val clazz = classAttr.value(inst.value(classAttr).toInt)
					ret = (probability, clazz)
				case x => //do nothing
			}
		}
		ret
	}

}
