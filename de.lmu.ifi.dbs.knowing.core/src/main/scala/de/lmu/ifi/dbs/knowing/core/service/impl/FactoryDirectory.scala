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
package de.lmu.ifi.dbs.knowing.core.service.impl

import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import scala.collection.mutable.HashMap
import org.slf4j.LoggerFactory
import de.lmu.ifi.dbs.knowing.core.internal.Activator

/**
 * Default implemenation for IFactoryDirectory
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
class FactoryDirectory extends IFactoryDirectory {

    private val log = LoggerFactory.getLogger(classOf[FactoryDirectory])

    /** 0..n relation */
    private val factories = new HashMap[String, TFactory]

    def getFactory(id: String): Option[TFactory] = factories.get(id)

    def getFactories(): Array[TFactory] = factories map (_._2) toArray

    def bindFactoryService(service: TFactory) {
        factories.contains(service.id) match {
            case true  => log.warn("Factory already exists: " + service.id + " . Using old one.")
            case false => factories += (service.id -> service)
        }
    }

    def unbindFactoryService(service: TFactory) = factories -= service.id

}