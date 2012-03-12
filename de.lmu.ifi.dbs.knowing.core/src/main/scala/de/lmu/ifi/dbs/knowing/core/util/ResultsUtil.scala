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
	/* =================== Result Creation =================== */
	/* ======================================================= */

	/**
	 * <p>Empty Instances. This Instances object has
	 * one Attribute (ATTRIBUTE_DUMMY) as you can't load
	 * an Instances object without any attributes</p>
	 *
	 * @return Instances with Attribute "dummy"
	 */
	def emptyResult: Instances = {
		val attributes = new ArrayList[Attribute]
		attributes.add(new Attribute(ATTRIBUTE_DUMMY))
		new Instances(NAME_EMPTY, attributes, 0)
	}

	/**
	 * <p>
	 * <li>relation name: {@link #NAME_CLASS_ONLY}</li>
	 * <li>attributes: {@link #ATTRIBUTE_CLASS}</li>
	 * </p>
	 * @param labels
	 * @return {@link Instances}
	 */
	def classOnlyResult(labels: List[String]): Instances = {
		val attributes = new ArrayList[Attribute]
		val classAttribute = new Attribute(ATTRIBUTE_CLASS, labels)
		attributes.add(classAttribute)
		val returns = new Instances(NAME_CLASS_ONLY, attributes, 0)
		returns.setClass(classAttribute)
		returns
	}

	/**
	 * <p>
	 * <li>relation name: {@link #NAME_CLASS_ONLY}</li>
	 * <li>attributes: {@link #ATTRIBUTE_CLASS}</li>
	 * </p>
	 * @param labels
	 * @return {@link Instances}
	 */
	def classOnlyResult(labels: JList[String]): Instances = classOnlyResult(labels.toList)

	/**
	 * <p>
	 * <li>relation name: {@link #NAME_CLASS_AND_PROBABILITY}</li>
	 * <li>attributes: {@link #ATTRIBUTE_CLASS}, {@link #ATTRIBUTE_PROBABILITY}</li>
	 * </p>
	 *
	 * @param labels
	 * @return {@link Instances} with {@link Attribute}s: "class" and "probability"
	 */
	def classAndProbabilityResult(labels: List[String]): Instances = {
		val attributes = new ArrayList[Attribute]

		val classAttribute = new Attribute(ATTRIBUTE_CLASS, labels)
		val probaAttribute = new Attribute(ATTRIBUTE_PROBABILITY)
		attributes.add(classAttribute)
		attributes.add(probaAttribute)

		val returns = new Instances(NAME_CLASS_AND_PROBABILITY, attributes, 0)
		returns.setClass(classAttribute)
		returns
	}

	/**
	 * <p>
	 * <li>relation name: {@link #NAME_CLASS_AND_PROBABILITY}</li>
	 * <li>attributes: {@link #ATTRIBUTE_CLASS}, {@link #ATTRIBUTE_PROBABILITY}</li>
	 * </p>
	 *
	 * @param labels
	 * @return {@link Instances} with {@link Attribute}s: "class" and "probability"
	 */
	def classAndProbabilityResult(labels: JList[String]): Instances = classAndProbabilityResult(labels.toList)

	/**
	 * <p>Adds [code]distribution.length[/code] instances to the dataset.<p>
	 * <p>The lables list must have the same ordering as the distribution array</p>
	 *
	 * @param labels
	 * @param distribution
	 * @return
	 */
	def classAndProbabilityResult(labels: List[String], distribution: Array[Double]): Instances = {
		val returns = classAndProbabilityResult(labels)
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

	/**
	 * <p>Adds [code]distribution.length[/code] instances to the dataset.<p>
	 * <p>The lables list must have the same ordering as the distribution array</p>
	 *
	 * @param labels
	 * @param distribution
	 * @return
	 */
	def classAndProbabilityResult(labels: JList[String], distribution: Array[Double]): Instances = classAndProbabilityResult(labels.toList)

	/**
	 * <p>Creates a Result-Instance for TimeInterval data</p>
	 * <p>
	 * Date | Date | Nominal <br>
	 * ##################### <br>
	 * from | to   | class   <br>
	 * </p>
	 * @param lables - class labels
	 * @param datePattern - which pattern should the Instances object use
	 * @return Instances
	 */
	def timeIntervalResult(labels: List[String], datePattern: String): Instances = {
		val attributes = new ArrayList[Attribute]
		attributes.add(new Attribute(ATTRIBUTE_FROM, datePattern))
		attributes.add(new Attribute(ATTRIBUTE_TO, datePattern))
		attributes.add(new Attribute(ATTRIBUTE_CLASS, labels))
		val dataset = new Instances(NAME_TIME_INTERVAL, attributes, 0)
		dataset.setClass(dataset.attribute(ATTRIBUTE_CLASS))
		dataset
	}

	/**
	 * @see timeIntervalResult()
	 */
	def timeIntervalResult(labels: List[String]): Instances = timeIntervalResult(labels, DATETIME_PATTERN)

	/**
	 * @see timeIntervalResult()
	 */
	def timeIntervalResult(labels: JList[String]): Instances = timeIntervalResult(labels.toList)

	/**
	 * @see timeIntervalResult()
	 */
	def timeIntervalResult(labels: JList[String], datePattern: String): Instances = timeIntervalResult(labels.toList, datePattern)

	/* ======================================================= */
	/* ================== Result validation ================== */
	/* ======================================================= */

	def isEmptyResult(dataset: Instances): Boolean = {
		dataset.relationName.equals(NAME_EMPTY) || (dataset.numAttributes == 1 && dataset.numInstances == 0)
	}

	/**
	 * @param dataset
	 * @return true - if structure equals to {@link #classOnlyResult(List)}
	 */
	def isClassOnlyResult(dataset: Instances): Boolean = {
		if (dataset.numAttributes() != 1)
			return false
		dataset.attribute(ATTRIBUTE_CLASS) != null
	}

	/**
	 * @param dataset
	 * @return true - if structure equals to {@link #classAndProbabilityResult(List)}
	 */
	def isClassAndProbabilityResult(dataset: Instances): Boolean = {
		if (dataset.numAttributes() != 2)
			return false
		val classAttribute = dataset.attribute(ATTRIBUTE_CLASS)
		val probaAttribute = dataset.attribute(ATTRIBUTE_PROBABILITY)
		classAttribute != null && probaAttribute != null
	}

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
