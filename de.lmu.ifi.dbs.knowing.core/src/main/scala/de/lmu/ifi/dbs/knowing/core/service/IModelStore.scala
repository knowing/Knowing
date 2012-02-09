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

import de.lmu.ifi.dbs.knowing.core.model.INode
import java.net.URL

/**
 * Service interface for a class which handles default models
 * provided by bundles or repositories.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait IModelStore {

	//Service properties
	val LOAD_ALL = "knowing.modelstore.loadAll"

	/**
	 *
	 * @param node - INode which needs a model
	 * @return URI to model or None
	 */
	def getModel(node: INode): Option[URL]

	/**
	 * @param model - name of model file
	 * @return URI to model or None
	 */
	def getModel(model: String): Option[URL]
}

