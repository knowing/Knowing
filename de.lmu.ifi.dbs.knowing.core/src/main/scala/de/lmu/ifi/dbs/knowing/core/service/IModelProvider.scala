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
package de.lmu.ifi.dbs.knowing.core.service

import java.net.URL

/**
 * Service interface to provide DPU model resources.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait IModelProvider {

	/**
	 *
	 * @return Map with filename -> model
	 */
	def getModels(): java.util.Map[String, URL]

	/**
	 * @param name - filename of model
	 * @return URL to model
	 */
	def getModel(name: String): URL

}