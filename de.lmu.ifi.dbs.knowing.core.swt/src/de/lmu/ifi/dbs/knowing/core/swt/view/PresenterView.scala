package de.lmu.ifi.dbs.knowing.core.swt.view

import akka.actor.{ ActorRef, Actor, TypedActor }
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ArrayBlockingQueue

import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.custom.{ CTabFolder, CTabItem }
import org.eclipse.ui.part.ViewPart
import org.eclipse.ui.IPropertyListener
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.{ Status => JobStatus }

import de.lmu.ifi.dbs.knowing.core.swt.dialog.ProgressDialog
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.model._

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
class PresenterView extends ViewPart {

  val uifactory = TypedActor.newInstance(classOf[UIFactory], new PresenterUIFactory(this))
  val rendevouz = new SynchronousQueue[Composite]

  private var tabFolder: CTabFolder = _

  def createPartControl(parent: Composite) = tabFolder = new CTabFolder(parent, SWT.BOTTOM)

  def createNodeTab(node: INode) {
    tabFolder.getDisplay.asyncExec(new Runnable {
      def run {
        debug(uifactory, "Trying to create tab... ")
        val composite = createTab(node.getId.getContent)
        rendevouz put (composite)
      }
    })
  }

  /**
   * <p>Creates a tab with the given name and returns <br>
   * the composite which is controlled by the tabitem.</p>
   *
   * @param name - name of the tab
   * @return tab content composite
   */
  def createTab(name: String): Composite = {
    val tabItem = new CTabItem(tabFolder, SWT.NONE)
    tabItem.setText(name)
    val composite = new Composite(tabFolder, SWT.NONE)
    composite.setLayout(new FillLayout)
    tabItem.setControl(composite)
    composite
  }

  def clearTabs {
    val items = tabFolder.getItems();
    items foreach (item => item.dispose)
  }

  def setFocus() = tabFolder setFocus

  def update = {
    tabFolder.getDisplay.syncExec(new Runnable {
      def run = tabFolder.setSelection(0)
    })
  }

}

object PresenterView { val ID = "de.lmu.ifi.dbs.knowing.core.swt.presenterView" }

/**
 * This class was the first UIFactory implementation. Will be
 * improved by extending TabUIFactory. Almost all the code got
 * used in UIFactories.scala
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
class PresenterUIFactory(view: PresenterView) extends TypedActor with UIFactory {

  private var started = false
  private var dialog: ProgressDialog = _
  var supervisor: ActorRef = _

  def createContainer(node: INode): Composite = {
    debug(this, "CreateContainer with " + node)
    view.createNodeTab(node)
    debug(this, "Waiting for finish...")
    val parent = view.rendevouz.take
    debug(this, "Took parent: " + parent)
    parent
  }

  def update(actor: ActorRef, status: Status) {
    //Create ProgressDialog on first status
    if (!started || dialog == null || dialog.disposed) {
      dialog = new ProgressDialog(view.getSite.getShell)
      dialog.supervisor = this.supervisor
      view.getSite.getShell.getDisplay.asyncExec(
        new Runnable {
          def run = dialog.open
        })
      started = true
    }
    //Update Dialog in UI Thread
    view.getSite.getShell.getDisplay.asyncExec(
      new Runnable {
        def run = dialog.update(actor, status)
      })

    //Handle special status events
    status match {
      case UpdateUI() => view update
      case Shutdown() =>
        started = false
        supervisor = null
        debug(this, "Shutdown UI Factory")
      case _ =>
    }

  }

  def setSupervisor(supervisor: ActorRef) = this.supervisor = supervisor
}
