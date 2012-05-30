package de.lmu.ifi.dbs.knowing.core.swt.factory

import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory.{ CONTAINER_PROPERTY, UI_PROPERTY }
import de.lmu.ifi.dbs.knowing.core.model.INode
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil
import de.lmu.ifi.dbs.knowing.core.swt.dialog.ProgressDialog
import akka.actor.{ ActorSystem, ActorRef, ActorContext, TypedActor, TypedProps }
import akka.event.{ LogSource, Logging }
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.custom.{ CTabItem, CTabFolder }
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.FillLayout
import java.util.concurrent.SynchronousQueue
import java.util.Hashtable
import scala.collection.JavaConversions._
import com.typesafe.config.ConfigFactory

object UIFactories {

	/**
	 * @param parent
	 * @param id
	 */
	def newCompositeUIFactoryInstance(system: ActorSystem, parent: Composite, id: String): UIFactory[Composite] = {
		TypedActor(system).typedActorOf(TypedProps(classOf[UIFactory[Composite]], new CompositeUIFactory(parent, id)))
	}

	/**
	 * Creates TabUIFactory with style SWT.BOTTOM for CTabFolder.
	 * @param parent
	 * @param id
	 */
	def newTabUIFactoryInstance(system: ActorSystem, parent: Composite, id: String): UIFactory[Composite] = {
		TypedActor(system).typedActorOf(TypedProps(classOf[UIFactory[Composite]], new TabUIFactory(parent, id)))
	}

	/**
	 * @param parent
	 * @param style - SWT.TOP | SWT.BOTTOM , see CTabFolder
	 * @param id
	 */
	def newTabUIFactoryInstance(system: ActorSystem, parent: Composite, style: Int, id: String): UIFactory[Composite] = {
		TypedActor(system).typedActorOf(TypedProps(classOf[UIFactory[Composite]], new TabUIFactory(parent, id, style)))
	}

	/**
	 * @param parent
	 * @param id
	 */
	def newCompositeUIFactoryInstance(parent: Composite, id: String): UIFactory[Composite] = newCompositeUIFactoryInstance(system, parent, id)

	/**
	 * Creates TabUIFactory with style SWT.BOTTOM for CTabFolder.
	 * @param parent
	 * @param id
	 */
	def newTabUIFactoryInstance(parent: Composite, id: String): UIFactory[Composite] = newTabUIFactoryInstance(system, parent, id)

	/**
	 * @param parent
	 * @param style - SWT.TOP | SWT.BOTTOM , see CTabFolder
	 * @param id
	 */
	def newTabUIFactoryInstance(parent: Composite, style: Int, id: String): UIFactory[Composite] = newTabUIFactoryInstance(system, parent, style, id)

	def newServiceProperties(): Hashtable[String, String] = {
		val props = new Hashtable[String, String]
		props.put(UI_PROPERTY, UIFactory.SWT)
		props.put(CONTAINER_PROPERTY, classOf[Composite].getName)
		props
	}
	
	private def system: ActorSystem = {
		val config = ConfigFactory.defaultReference(classOf[ActorSystem].getClassLoader)
		ActorSystem("default", config)
	}

}

/**
 * Abstract default class to implement own SWT UIFactories.
 * Provides a simple ProgressDialog.
 *
 * @author Nepmuk Seiler
 * @version 1.0
 */
abstract class SwtUIFactory(parent: Composite, id: String) extends UIFactory[Composite] {

	private val rendevouz = new SynchronousQueue[Composite]

	implicit val logSourceType: LogSource[UIFactory[Composite]] = new LogSource[UIFactory[Composite]] {
		def genString(a: UIFactory[Composite]) = a.getId
	}
	protected val log = Logging(TypedActor.context.system, this)

	private var started = false
	private var dialog: ProgressDialog = _
	var supervisorContext: ActorContext = _

	/**
	 * creates a {@link Composite} container and takes
	 * care of sync with the UI thread.
	 */
	def createContainer(node: INode): Composite = {
		log.debug("CreateContainer with [" + node + "]  Waiting for finish.")
		parent.getDisplay.asyncExec(new Runnable {
			def run {
				log.debug("Trying to create UI container on UI thread.")
				val composite = createControl(node)
				rendevouz put (composite)
			}
		})
		rendevouz.take
	}

	def update(actor: ActorRef, status: Status) {
		//Create ProgressDialog on first status
		if (!started || dialog == null || dialog.disposed) {
			val dialogRendenzvouz = new SynchronousQueue[ProgressDialog]
			parent.getDisplay.asyncExec(
				new Runnable {
					def run = {
						val dialog = new ProgressDialog(parent.getShell)
						dialog.supervisorContext = supervisorContext
						dialog.open
						dialogRendenzvouz put (dialog)
					}
				})
			started = true
			dialog = dialogRendenzvouz.take
		}
		//Update Dialog in UI Thread
		parent.getDisplay.asyncExec(
			new Runnable {
				def run = dialog.update(actor, status)
			})

		//Handle special status events
		status match {
			case UpdateUI() =>
				parent.getDisplay.syncExec(
					new Runnable {
						def run = updateUI()
					})
			case Shutdown() => started = false
			case _ =>
		}
	}

	def setSupervisorContext(supervisorContext: ActorContext) = {
		this.supervisorContext = supervisorContext
		parent.getDisplay.syncExec(
			new Runnable {
				def run = disposeControls()
			})

	}

	/**
	 * Id which is passed as an parameter in the constructor
	 */
	def getId(): String = id

	/**
	 * Forces the UI to repaint
	 */
	def updateUI() = parent.layout()

	/**
	 * Extending classes must implement this method.
	 * It's synchronized with the UI thread.
	 */
	def createControl(node: INode): Composite

	/**
	 * A new datamining process is started. Dispose
	 * old UI containers.
	 * It's synchronized with the UI thread.
	 */
	def disposeControls()

}

/**
 * Simply returns a new composite.
 * @param parent - parent of all composites created
 *
 * @author Nepmuk Seiler
 * @version 1.0
 */
class CompositeUIFactory(parent: Composite, id: String) extends SwtUIFactory(parent, id) {

	private var composite: Composite = _

	def createControl(node: INode): Composite = {
		composite = new Composite(parent, SWT.NONE)
		composite.setLayout(new FillLayout)

		//TODO: Configure LayoutManager with properties
		//DPUUtil.nodeProperties(node)
		composite
	}

	def disposeControls() = {
		composite.dispose()
		composite = null
	}
}

/**
 * Creates a tab for each node with node.id.
 * Default SWT Style for CTabFolder is SWT.BOTTOM.
 *
 * @author Nepmuk Seiler
 * @version 1.0
 *
 */
class TabUIFactory(parent: Composite, id: String, style: Int = SWT.BOTTOM) extends SwtUIFactory(parent, id) {

	private var tabFolder: CTabFolder = _

	/**
	 * <p>Creates a tab with the given name and returns <br>
	 * the composite which is controlled by the tabitem.</p>
	 *
	 * @param name - name of the tab
	 * @return tab content composite
	 */
	def createControl(node: INode): Composite = {
		if (tabFolder == null) {
			tabFolder = new CTabFolder(parent, style)
			parent layout
		}

		val tabItem = new CTabItem(tabFolder, SWT.NONE)
		tabItem.setText(node.getId.getContent)
		val composite = new Composite(tabFolder, SWT.NONE)
		composite.setLayout(new FillLayout)
		tabItem.setControl(composite)

		composite
	}

	/**
	 * Disposes all items inside the CTabFolder.
	 * Does not dispose the CTabFolder.
	 */
	def disposeControls() = if (tabFolder != null && !tabFolder.isDisposed) {
		tabFolder.getItems foreach (item => item.dispose)
	}

	/**
	 * Sets selection to the first tab if exists.
	 */
	override def updateUI() = {
		parent.layout
		if (tabFolder != null)
			tabFolder.getItemCount match {
				case 0 =>
				case _ => tabFolder.setSelection(0)
			}
	}

}
