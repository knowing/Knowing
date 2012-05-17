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
package de.lmu.ifi.dbs.knowing.core.factory

import akka.actor.{ ActorContext, ActorRef }
import de.lmu.ifi.dbs.knowing.core.events.Status
import de.lmu.ifi.dbs.knowing.core.model.INode

/**
 * <p>Provides the hook between the datamining process and the UI.</p>
 *
 * <p>Each presenter receives the UIFactory and tries to create a
 * UI container via the `createContainer(node: INode):T` method.
 * Implementations should always be aware of thread-safety</p>
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
trait UIFactory[T] {

	/**
	 * Called by the TPresenter[T] to create its UI container.
	 * This method has to run on the UI thread, so take care
	 * of thread-safety.
	 *
	 * @param node - the DPU node to gain additional information (node ID, name property)
	 * @returns T - the UI container (e.g. JPanel, Composite)
	 */
	def createContainer(node: INode): T

	/**
	 * The DPUExecutor, actor supervisor, informs the UI factory about
	 * progress changes. Updates the UI.
	 *
	 * This method handles failures and display exceptions. For this
	 * the UIFactory gets a reference to the supervisor to shutdown
	 * the process.
	 *
	 * @param actor - who send the change
	 * @param status - what changed
	 * @see de.lmu.ifi.dbs.knowing.core.events.Event
	 */
	def update(actor: ActorRef, status: Status)

	/**
	 * Sets supervisor and thus indicates that a new
	 * process starts. Use this method to refresh the
	 * UIFactory.
	 *
	 * @param supervisor - ActorRef to DPUExecutor
	 */
	def setSupervisorContext(supervisorContext: ActorContext)

	/**
	 * ID is to distinguish between multiple UIFactories
	 * registered. It's recommended to use the plugin id
	 * or View ID (if using Eclipse RCP) for the UIFactory.
	 *
	 * @return id - references id for this UIFactory
	 */
	def getId(): String

}

/**
 * @see https://github.com/knowing/Knowing/wiki/UIFactory
 */
object UIFactory {
	//Properties
	val UI_PROPERTY = "ui"
	val CONTAINER_PROPERTY = "container"

	//UI systems
	val SWT = "SWT"
	val SWING = "SWING"
	val AWT = "AWT"
	val VAADIN = "VAADIN"
	val GWT = "GWT"

}