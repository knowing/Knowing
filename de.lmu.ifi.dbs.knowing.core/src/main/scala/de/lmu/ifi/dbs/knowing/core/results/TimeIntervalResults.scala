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

import java.util.{ ArrayList, Arrays, Collections, List => JList, Properties, Map => JMap }
import weka.core.{ Attribute, ProtectedProperties, Instances }
import scala.collection.JavaConversions._

object TimeIntervalResults extends ResultsType {
	
	val name = "time_interval"

	val ATTRIBUTE_FROM = ResultsType.ATTRIBUTE_FROM
	val ATTRIBUTE_TO = ResultsType.ATTRIBUTE_TO
	val ATTRIBUTE_CLASS = ResultsType.ATTRIBUTE_CLASS
	
	val DATETIME_PATTERN = ResultsType.DATETIME_PATTERN
	
	def newInstances(): Instances = { null }

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
	def newInstances(classes: List[String], datePattern: String): Instances = {
		val attributes = new ArrayList[Attribute]
		attributes.add(new Attribute(ATTRIBUTE_FROM, datePattern))
		attributes.add(new Attribute(ATTRIBUTE_TO, datePattern))
		attributes.add(new Attribute(ATTRIBUTE_CLASS, classes))
		val dataset = new Instances(name, attributes, 0)
		dataset.setClass(dataset.attribute(ATTRIBUTE_CLASS))
		dataset
	}
	
	def apply(classes: List[String], datePattern: String): Instances = newInstances(classes, datePattern)

	/**
	 * @see timeIntervalResult()
	 */
	def newInstances(classes: List[String]): Instances = newInstances(classes, DATETIME_PATTERN)
	
	def apply(classes: List[String]): Instances = newInstances(classes)

	/**
	 * @see timeIntervalResult()
	 */
	def newInstances(classes: JList[String]): Instances = newInstances(classes.toList)

	/**
	 * @see timeIntervalResult()
	 */
	def newInstances(classes: JList[String], datePattern: String): Instances = newInstances(classes.toList, datePattern)

}