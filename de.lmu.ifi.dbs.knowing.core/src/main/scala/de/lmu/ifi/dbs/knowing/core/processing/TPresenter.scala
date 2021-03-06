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

import akka.actor.{ Actor, ActorRef }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.exceptions._
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import scala.collection.mutable.{ Set => MSet }
import scala.collection.JavaConversions._
import weka.core.{ Instances, Instance, Attribute }

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 18.04.2011
 */
trait TPresenter[T] extends TProcessor {

	/** Name of this component */
	val name: String
	
	override final protected def customReceive: Receive = presenterReceive orElse defaultReceive

	protected def presenterReceive: Receive = defaultReceive

	/**
	 * Handles UI specific events
	 */
	private def defaultReceive: Receive = {
		case UIFactoryEvent(factory, node) =>
			statusChanged(Running())
			val parent = factory.createContainer(node).asInstanceOf[T]
			sync(parent) {
				createContainer(parent)
			}

			sender ! Ready()
			statusChanged(Ready())
	}

	/**
	 * Executes the given function on the UI Thread synchronous.
	 *
	 * @param parent - used to access display
	 * @param syncFun - function which will be executed on the UI thread
	 */
	def sync(parent: T)(syncFun: => Unit)

	/**
	 * Executes the given runnable on the UI Thread synchronous.
	 *
	 * @param parent - used to access display
	 * @param runnable - runnable which will be executed on the UI thread
	 */
	def sync(parent: T, runnable: Runnable)

	/**
	 *
	 */
	def process(instances: Instances) = {
		//(None, _) | (Some(DEFAULT_PORT), _)
		case _ =>
			statusChanged(Running())
			sync(getParent) {
				buildPresentation(instances)
			}
			statusChanged(UpdateUI())
			statusChanged(Ready())
	}

	/**
	 * Creates the initial UI-Container presenting the data
	 *
	 * @param parent
	 * @return
	 */
	def createContainer(parent: T)

	/**
	 * @param instances
	 */
	def buildPresentation(instances: Instances)

	/**
	 * <p>This method is used for determining if the<br>
	 * UI system is able to handle this presenter.</p>
	 *
	 * @return the UI-Container class
	 */
	def getContainerClass(): String

	/**
	 * Returns the parent of this presenter
	 */
	def getParent(): T

	/* == Doesn't needed by TPresenter == */
	@throws(classOf[KnowingException])
	def query(query: Instances): Instances = throw new UnsupportedOperationException("Presenter doesn't support queries")

}