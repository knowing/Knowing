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

/**
 * Format for TimeSeresResults. Contain TIMESTAMP and VALUE attributes.
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
object TimeSeriesResults extends ResultsType with ValueResultsType {

	val name = "time_series"

	val ATTRIBUTE_TIMESTAMP = ResultsType.ATTRIBUTE_TIMESTAMP
	val ATTRIBUTE_VALUE = ResultsType.ATTRIBUTE_VALUE
	val ATTRIBUTE_VALUE_PREFIX = ResultsType.ATTRIBUTE_VALUE_PREFIX
	
	val META_ATTRIBUTE_NAME = ResultsType.META_ATTRIBUTE_NAME

	val DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss:SSS"
	
	/**
	 * Format:
	 * <p>ATTRIBUTE_TIMESTAMP | ATTRIBUTE_VALUE<p>
	 * 
	 * <p>DATETIME_PATTERN: yyyy-MM-dd'T'HH:mm:ss:SSS</p>
	 * 
	 * @return TimeSeries Instances with one value column
	 */
	def newInstances(): Instances = {
		val attributes = new ArrayList[Attribute]
		attributes.add(new Attribute(ATTRIBUTE_TIMESTAMP, DATETIME_PATTERN))
		attributes.add(new Attribute(ATTRIBUTE_VALUE))
		new Instances(name, attributes, 0)
	}

	/**
	 * <p>
	 * Creates an Instances object with a DATE column and [code]names.size()[/code]
	 * nummeric attributes. [br] All numeric attributes provide meta data with one
	 * property {@link #META_ATTRIBUTE_NAME}.
	 * <li>relation name: {@link #NAME_DATE_AND_VALUES}
	 * <li>attributes: {@link #ATTRIBUTE_TIMESTAMP}, {@link #ATTRIBUTE_VALUE}+index
	 * </p>
	 * @param names - the numeric attributes names -] accessible via meta data
	 * @return
	 */
	def newInstances(names: List[String], datePattern: String): Instances = {
		val attributes = new ArrayList[Attribute]

		attributes.add(new Attribute(ATTRIBUTE_TIMESTAMP, datePattern))
		for (i <- 0 until names.size) {
			val props = new Properties()
			props.setProperty(META_ATTRIBUTE_NAME, names.get(i))
			val attribute = new Attribute(ATTRIBUTE_VALUE + i, new ProtectedProperties(props))
			attributes.add(attribute)
		}
		new Instances(name, attributes, 0)
	}
	
	def apply(names: List[String], datePattern: String): Instances = newInstances(names, datePattern)

	/**
	 * @see timeSeriesResult(names, datePattern)
	 */
	def newInstances(names: List[String]): Instances = newInstances(names, DATETIME_PATTERN)
	
	def apply(names: List[String]): Instances = newInstances(names)

	/**
	 * @see timeSeriesResult(names, datePattern)
	 */
	def newInstances(names: JList[String]): Instances = newInstances(names.toList)

	/**
	 * @see timeSeriesResult(names, datePattern)
	 */
	def newInstances(names: JList[String], datePattern: String): Instances = newInstances(names.toList, datePattern)
}