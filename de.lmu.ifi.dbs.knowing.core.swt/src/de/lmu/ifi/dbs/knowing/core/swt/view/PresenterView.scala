/**
 *
 */
package de.lmu.ifi.dbs.knowing.core.swt.view

import akka.actor.TypedActor
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Actor.actorOf

import java.util.concurrent.SynchronousQueue
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.{ Composite, TabFolder, TabItem }
import org.eclipse.swt.graphics.Image
import org.eclipse.ui.part.ViewPart
import org.eclipse.ui.IPropertyListener
import de.lmu.ifi.dbs.knowing.core.graph.Node
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.events.UIContainer

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
class PresenterView extends ViewPart {

  val uifactory = TypedActor.newInstance(classOf[UIFactory], new PresenterUIFactory(this))
  var tabFolder: TabFolder = _
  
  val rendevouz = new SynchronousQueue[Composite]

  def createPartControl(parent: Composite) = tabFolder = new TabFolder(parent, SWT.BOTTOM);

  def createNodeTab(node: Node) = {
    tabFolder.getDisplay.asyncExec(new Runnable() {
      def run() {
        println("Trying to create tab... ")
        val composite = createTab(node.id)
        rendevouz put(composite)
        println("... " + composite + " tab created")
      }
    });
  }

  /**
   * <p>Creates a tab with the given name and returns <br>
   * the composite which is controlled by the tabitem.</p>
   *
   * @param name - name of the tab
   * @return tab content composite
   */
  def createTab(name: String): Composite = {
    val tabItem = new TabItem(tabFolder, SWT.NONE);
    tabItem.setText(name);
    val composite = new Composite(tabFolder, SWT.NONE);
    composite.setLayout(new FillLayout());
    tabItem.setControl(composite);
    composite;
  }

  def clearTab {
    val items = tabFolder.getItems();
    items foreach (item => item.dispose)
  }

  def setFocus() = tabFolder setFocus

}

object PresenterView { val ID = "de.lmu.ifi.dbs.knowing.core.swt.presenterView" }

class PresenterUIFactory(view:PresenterView) extends TypedActor with UIFactory {
  
  def createContainer(node: Node):Composite = {
    log debug("CreateContainer with " + node)
    view.createNodeTab(node)
    log debug("Waiting for finish...")
    val parent = view.rendevouz.take()
    log debug("Took parent: " + parent)
    parent
  }
}