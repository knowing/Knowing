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
import scala.collection.JavaConversions._
import weka.core.{Attribute,Instances}
import ResultsType.{ATTRIBUTE_VALUE,ATTRIBUTE_VALUE_PREFIX, META_ATTRIBUTE_NAME}

/**
 * Mixin with ResultTypes that contain value attributes
 * created with ATTRIBUTE_VALUE_PREFIX to provide easy
 * access methods. 
 * 
 * @author Nepomuk Seiler
 * @verison 0.1
 */
trait ValueResultsType { this: ResultsType => 

	/**
	 *
	 * @param dataset
	 * @return list with all numeric attributes created with {@link #ATTRIBUTE_VALUE} naming scheme
	 */
	def findValueAttributes(dataset: Instances): JList[Attribute] = {
		val returns = new ArrayList[Attribute]
		var i = 0
		var attribute = dataset.attribute(ATTRIBUTE_VALUE_PREFIX + i)
		while (attribute != null) {
			returns.add(attribute)
			i += 1
			attribute = dataset.attribute(ATTRIBUTE_VALUE_PREFIX + i)
		}
		returns

	}

	/**
	 *
	 * @param dataset
	 * @return map with META_ATTRIBUTE_NAME -> Attribute
	 */
	def findValueAttributesAsMap(dataset: Instances): Map[String, Attribute] = {
		var returns = Map[String, Attribute]()
		var i = 0
		var attribute = dataset.attribute(ATTRIBUTE_VALUE_PREFIX + i)
		while (attribute != null) {
			val name = attribute.getMetadata.getProperty(META_ATTRIBUTE_NAME)
			if (name == null || name.isEmpty)
				returns += (attribute.name -> attribute)
			else
				returns += (name -> attribute)
			i += 1
			attribute = dataset.attribute(ATTRIBUTE_VALUE_PREFIX + i)
		}
		returns
	}

	/**
	 *
	 * @param dataset
	 * @return map with META_ATTRIBUTE_NAME -> Attribute
	 */
	def findValueAttributesAsJavaMap(dataset: Instances): java.util.Map[String, Attribute] = findValueAttributesAsMap(dataset).toMap[String, Attribute]
}