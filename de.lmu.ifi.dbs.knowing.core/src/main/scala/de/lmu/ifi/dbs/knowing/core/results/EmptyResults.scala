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

import weka.core.{Attribute,Instances}
import java.util.ArrayList

/**
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 12.03.2012
 */
object EmptyResults extends ResultsType {

	val name = "empty"

	val ATTRIBUTE_EMPTY = "empty"

	/**
	 * <p>Empty Instances. This Instances object has
	 * one Attribute (ATTRIBUTE_DUMMY) as you can't load
	 * an Instances object without any attributes</p>
	 *
	 * @return Instances with Attribute "dummy"
	 */
	def newInstances: Instances = {
		val attributes = new ArrayList[Attribute]
		attributes.add(new Attribute(ATTRIBUTE_EMPTY))
		new Instances(name, attributes, 0)
	}

}