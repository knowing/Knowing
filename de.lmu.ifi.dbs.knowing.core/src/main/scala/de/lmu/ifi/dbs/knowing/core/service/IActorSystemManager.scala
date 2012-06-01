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
package de.lmu.ifi.dbs.knowing.core.service

import akka.actor.ActorSystem
import com.typesafe.config.Config

/**
 * Provides a way to managed running ActorSystems.
 *
 * The ActorSystem name is treated as a unique identifier
 * and with this the service makes it possible to search
 * for existing ActorSystems.
 *
 *
 * @author Nepomuk Seiler
 * @version 0.1.4
 * @since 2012-05-13
 */
trait IActorSystemManager {

	/**
	 * @param name of the system
	 * @return None if system not created yet
	 */
	def getSystem(name: String): Option[ActorSystem]

	/**
	 * Returns a fresh ActorSystem, shutting down the
	 * old one if existed.
	 *
	 * @param name of the system
	 * @param config - to startup system
	 * @return new system or existing one if already existed
	 */
	def create(name: String, config: Config): ActorSystem

	/**
	 * Returns a fresh ActorSystem, shutting down the
	 * old one if existed.
	 *
	 * @param name of the system
	 * @param config - to startup system
	 * @param classLoader - 
	 * @return new system or existing one if already existed
	 */
	def create(name: String, config: Config, classloader: ClassLoader): ActorSystem

	/**
	 * @return list of all available ActorSystems
	 */
	def getSystems(): java.util.List[ActorSystem]

	/**
	 * Proper way to shutdown actor system.
	 * Informs all listeners of this system.
	 */
	def shutdownSystem(name: String)

	/**
	 * Listen on create and shutdown events from
	 * ActorSystems.
	 */
	def addListener(listener: ActorSystemListener)

	/**
	 * Removes listener. If not existend, nothing
	 * happens.s
	 */
	def removeListener(listener: ActorSystemListener)
}

trait ActorSystemListener {

	def onSystemCreate(system: ActorSystem)

	def onSystemShutdown(system: ActorSystem)
}