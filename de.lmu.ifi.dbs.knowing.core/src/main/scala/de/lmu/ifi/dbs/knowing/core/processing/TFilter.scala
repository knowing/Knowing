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
package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Properties
import weka.core.{ Instances, Instance }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.{ appendInstances, emptyResult }

/**
 *  <p>Used to filter Instances</p>
 *
 *  @author Nepomuk Seiler
 *  @version 0.3
 *  @since 16.06.2011
 */
trait TFilter extends TProcessor {

	//Filter is always build. Set this to false if filter has to be trained
	isBuild = true

	/**
	 *
	 */
	@throws(classOf[KnowingException])
	def filter(instances: Instances): Instances

	/**
	 * delegate to TFilter.filter(instances)
	 */
	def query(instances: Instances): Instances = filter(instances)

	/**
	 * Delegates to filter method
	 */
	def process(instances: Instances) = {
		case (None, _) | (Some(DEFAULT_PORT),_) => sendResults(filter(instances))
		case (Some(port), _) => throwException(new KnowingException("Unkown port[" + port + "]in filter."))
	}

	//TODO TFilter => Input/Output Format configuration

}