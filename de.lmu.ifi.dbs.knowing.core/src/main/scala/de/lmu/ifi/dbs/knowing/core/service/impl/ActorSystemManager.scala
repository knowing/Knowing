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
package de.lmu.ifi.dbs.knowing.core.service.impl

import de.lmu.ifi.dbs.knowing.core.service.IActorSystemManager
import de.lmu.ifi.dbs.knowing.core.service.ActorSystemListener
import com.typesafe.config.Config
import akka.actor.ActorSystem
import org.osgi.service.component.ComponentContext
import scala.collection.mutable.{ HashSet, HashMap }
import scala.collection.JavaConversions._
import org.slf4j.LoggerFactory

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
class ActorSystemManager extends IActorSystemManager {
	
	val log = LoggerFactory.getLogger(classOf[IActorSystemManager])

	private val systems = new HashMap[String, ActorSystem]()
	private val listeners = new HashSet[ActorSystemListener]

	private var context: ComponentContext = _

	def getSystem(name: String): Option[ActorSystem] = systems.get(name)

	def create(name: String, config: Config): ActorSystem = {
		systems.get(name).foreach(_.shutdown)
		val system = ActorSystem(name, config)
		systems += (name -> system)
		system
	}

	def create(name: String, config: Config, classloader: ClassLoader): ActorSystem = {
		systems.get(name).foreach(_.shutdown)
		val system = ActorSystem(name, config, classloader)
		systems += (name -> system)
		system
	}

	def getSystems(): java.util.List[ActorSystem] = systems.values.toList

	def shutdownSystem(name: String) = systems.remove(name).foreach {
		system =>
			fireEvent(_.onSystemShutdown(system))
			system.shutdown
	}

	/* ================================== */
	/* ======= Listener Support ========= */
	/* ================================== */

	def addListener(listener: ActorSystemListener) = listeners += listener

	def removeListener(listener: ActorSystemListener) = listeners -= listener

	protected def fireEvent(fn: ActorSystemListener => Unit) = listeners foreach (fn(_))

	/* ================================== */
	/* ======= Service Life Cycle ======= */
	/* ================================== */

	protected def activate(context: ComponentContext) {
		//TODO maybe use this to register ActorSystems as OSGi Service?
		this.context = context
		log.debug("Activate ActorSystemManager")
	}

	protected def deactivate() {
		log.debug("Deactivate ActorSystemManager")
	}

}