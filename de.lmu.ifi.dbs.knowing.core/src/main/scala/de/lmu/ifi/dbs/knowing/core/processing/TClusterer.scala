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
package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Properties
import weka.core.Instances
import de.lmu.ifi.dbs.knowing.core.processing.IProcessorPorts.{ TRAIN, TEST }

/**
 * @author Nepomuk Seiler
 * @version 0.2.0
 * @since 2012-05-22
 */
trait TClusterer extends TProcessor {

	def process(instances: Instances) = {
		case (Some(TEST), None) => isBuild match {
				case false => cacheQuery(instances)
				case true =>
					processStoredQueries
					val results = query(instances)
					sendResults(results, None, Some(instances))
			}
		case (None, None) | (Some(TRAIN), None) =>
		case (Some(port), _) => log.warning("Incompatible port")
	}

	def buildClusterer(data: Instances)

}