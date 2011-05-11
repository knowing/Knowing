/**
 *
 */
package de.lmu.ifi.dbs.knowing.core.swt

import java.util.Properties
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.swt.provider.{ InstanceContentProvider, InstanceLabelProvider }
import org.eclipse.jface.viewers.{ TableViewerColumn, TableViewer }
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import weka.core.{ Instances, Attribute }

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
class TablePresenter extends SWTPresenter {

  val name = TablePresenter.name

  private var viewer: TableViewer = _
  private var columnsInit = false
  private var rows = 100

  def createControl(parent: Composite) = viewer = new TableViewer(parent)

  def buildContent(instances: Instances) = {
    log debug ("buildContent...")
    createColumns(instances.enumerateAttributes());
    viewer.setInput(createInput(instances));
    viewer.refresh();
    log debug ("... content build")
  }

  def getModel(labels: Array[String]): Instances = { null }

  def createColumns(eAttr: java.util.Enumeration[_]) {
    log debug ("createColumns...")
    if (columnsInit)
      return ;
    viewer.getTable().setHeaderVisible(true);
    viewer.getTable().setLinesVisible(true);
    while (eAttr.hasMoreElements) {
      val a = eAttr.nextElement().asInstanceOf[Attribute]
      val viewerColumn = new TableViewerColumn(viewer, SWT.LEAD)
      viewerColumn.getColumn().setText(a.name())
      viewerColumn.getColumn().setWidth(70)
      viewerColumn.getColumn().setResizable(true)
      viewerColumn.getColumn().setMoveable(true)
    }
    viewer.setLabelProvider(new InstanceLabelProvider());
    viewer.setContentProvider(new InstanceContentProvider(true));
    columnsInit = true;
    log debug ("... columns created")
  }
  
  def configure(properties:Properties) = {
    log debug ("Configure TablePresenter with " + properties)
    val row_string = properties.getProperty(TablePresenter.ROWS_PER_PAGE)
    rows = Integer.getInteger(row_string)
  }
  
  private def createInput(instances: Instances):Instances = {
    new Instances(instances, 0, rows)
  }

}

object TablePresenter {

  val ROWS_PER_PAGE = "rows"
  val name = "Table Presenter"
}

class TablePresenterFactory extends TFactory {

  val name: String = TablePresenter.name
  val id: String = classOf[TablePresenter].getName

  def getInstance(): ActorRef = actorOf[TablePresenter]

  def createDefaultProperties: Properties = {
    val properties = new Properties
    properties.setProperty(TablePresenter.ROWS_PER_PAGE, "100");
    properties
  }

  def createPropertyValues: Map[String, Array[Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()
}