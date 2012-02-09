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
 * Provides default resources for a datamining process, e.g
 * input files, static non-model resources.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait IResourceProvider {

	/**
	 *
	 * @return Map with filename -> model
	 */
	def getResources(): java.util.Map[String, URL]

	/**
	 * @param name - filename of model
	 * @return URI to model
	 */
	def getResource(name: String): URL

}