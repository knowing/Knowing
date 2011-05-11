/**
 *
 */
package de.lmu.ifi.dbs.knowing.core.swt

import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import java.util.Properties

import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.{ Composite, TabFolder, TabItem }
import weka.core.Instances

/**
 * <p>MultiTablePresenter holds multiple TablePresenter in a map.<br>
 * The {@link Instances#relationName()} works as a key.</p>
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 22.04.2011
 *
 */
class MultiTablePresenter extends SWTPresenter {

  val name = MultiTablePresenter.name

  private var tables: Map[String, ActorRef] = Map()
  private var tabFolder: TabFolder = _

  def createControl(parent: Composite) = tabFolder = new TabFolder(parent, SWT.TOP);

  def buildContent(instances: Instances) = {
    val relation = instances.relationName
    val presenter = tables.get(relation)
    presenter match {
      case Some(p) => p ! Results(instances)
      case None => //Create new container
        log debug ("Creating new TablePresenter...")
        val p = actorOf[TablePresenter].start
        log debug ("... and createContainer for " + p.getActorClassName)
        p ! UIContainer(createTab(relation))
        log debug ("... and build content for " + p.getActorClassName)
        p ! Results(instances)
        tables += (relation -> p)
    }
  }

  /**
   * <p>Creates a tab with the given name and returns <br>
   * the composite which is controlled by the tabitem.</p>
   *
   * @param name - name of the tab
   * @return tab content composite
   */
  private def createTab(name: String): Composite = {
    val tabItem = new TabItem(tabFolder, SWT.NONE)
    tabItem.setText(name)
    val composite = new Composite(tabFolder, SWT.NONE)
    composite.setLayout(new FillLayout())
    tabItem.setControl(composite)
    log debug ("Composite created: " + composite)
    composite
  }

  def configure(properties: Properties) = {}

  def getModel(labels: Array[String]): Instances = { null }

}

object MultiTablePresenter { val name = "MultiTable Presenter" }

class MultiTablePresenterFactory extends TFactory {

  val name: String = MultiTablePresenter.name
  val id: String = classOf[MultiTablePresenter].getName

  def getInstance(): ActorRef = actorOf[MultiTablePresenter]

  def createDefaultProperties: Properties = new Properties

  def createPropertyValues: Map[String, Array[Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()

}