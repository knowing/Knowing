package de.lmu.ifi.dbs.knowing.core.swt.factory

import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.model.INode
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.swt.dialog.ProgressDialog
import akka.actor.ActorRef
import akka.actor.TypedActor
import akka.event.EventHandler.{ debug, info, warning, error }
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.layout.FillLayout
import java.util.concurrent.SynchronousQueue

object UIFactories {

}

/**
 * Abstract default class to implement own SWT UIFactories.
 * Provides a simple ProgressDialog.
 */
abstract class SwtUIFactory(parent: Composite) extends TypedActor with UIFactory {

  private val rendevouz = new SynchronousQueue[Composite]
  private var started = false
  private var dialog: ProgressDialog = _
  var supervisor: ActorRef = _

  def createContainer(node: INode): Composite = {
    debug(this, "CreateContainer with " + node + " ... waiting for finish...")
    parent.getDisplay.asyncExec(new Runnable {
      def run {
        debug(this, "Trying to create tab... ")
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
            dialog.supervisor = supervisor
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
        parent.getDisplay.asyncExec(
          new Runnable {
            def run = parent.layout()
          })
      case Shutdown() => started = false
      case _ =>
    }
  }

  def setSupervisor(supervisor: ActorRef) = this.supervisor = supervisor

  /**
   * Extending classes must implement this method.
   * It's synchronized with the UI thread.
   */
  def createControl(node: INode): Composite

}

/**
 * Simply returns a new composite.
 * @param parent - parent of all composites created
 */
class CompositeUIFactory(parent: Composite) extends SwtUIFactory(parent) {

  def createControl(node: INode): Composite = {
    val composite = new Composite(parent, SWT.NONE)
    composite.setLayout(new FillLayout)
    composite
  }
}

/**
 * Creates a tab for each node with node.id.
 */
class TabUIFactory(parent: Composite, style: Int = SWT.BOTTOM) extends SwtUIFactory(parent) {

  private var tabFolder: CTabFolder = new CTabFolder(parent, style)

  /**
   * <p>Creates a tab with the given name and returns <br>
   * the composite which is controlled by the tabitem.</p>
   *
   * @param name - name of the tab
   * @return tab content composite
   */
  def createControl(node: INode): Composite = {
    val tabItem = new CTabItem(tabFolder, SWT.NONE)
    tabItem.setText(node.getId.getContent)
    val composite = new Composite(tabFolder, SWT.NONE)
    composite.setLayout(new FillLayout)
    tabItem.setControl(composite)
    composite
  }

  def clearTabs {
    val items = tabFolder.getItems();
    items foreach (item => item.dispose)
  }
}
