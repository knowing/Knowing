package de.lmu.ifi.dbs.knowing.core.swt

import java.util.Properties
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.swt.provider.{ InstanceContentProvider, InstanceLabelProvider }
import de.lmu.ifi.dbs.knowing.presenter.ITablePresenter
import org.eclipse.jface.viewers.{ TableViewerColumn, TableViewer }
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.{ Composite, Button, Label, Spinner,Listener }
import org.eclipse.swt.layout.{ GridData, GridLayout }
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.ColumnWeightData
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
  private var layout: TableColumnLayout = _
  private var columnsInit = false
  private var rows = 100

  def createControl(parent: Composite) {
    val composite = new Composite(parent, SWT.NONE)
    composite.setLayout(new GridLayout(4, false))

    val bLeft = new Button(composite, SWT.NONE)
    val gd_bLeft = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1)
    gd_bLeft.widthHint = 40
    gd_bLeft.minimumWidth = 40
    gd_bLeft.horizontalIndent = -1
    gd_bLeft.verticalIndent = -1
    bLeft.setLayoutData(gd_bLeft)
    bLeft.setText("<-")
    bLeft.setEnabled(false)

    val lPageIndex = new Label(composite, SWT.NONE)
    lPageIndex.setAlignment(SWT.CENTER)
    val gd_lPageIndex = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1)
    gd_lPageIndex.widthHint = 40
    lPageIndex.setLayoutData(gd_lPageIndex)
    lPageIndex.setText("- / -")

    val bRight = new Button(composite, SWT.NONE)
    val gd_button = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1)
    gd_button.widthHint = 40
    bRight.setLayoutData(gd_button)
    bRight.setText("->")
    bRight.setEnabled(false)

    val sRows = new Spinner(composite, SWT.BORDER)
    val gd_spinner = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1)
    gd_spinner.widthHint = 50
    sRows.setLayoutData(gd_spinner)
    sRows.setSelection(rows)

    val tableComposite = new Composite(composite, SWT.NONE)
    layout = new TableColumnLayout
    tableComposite.setLayout(layout)
    tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1))
    viewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION)
  }

  def buildContent(instances: Instances) {
    createColumns(instances)
    viewer.setInput(createInput(instances))
    viewer.refresh()
  }

  def createColumns(instances: Instances) {
    if (columnsInit)
      return

    debug(this, "createColumns...")
    instances.setClassIndex(-1); //WekaEnumeration skips the class attribute, so the class index has to be unset...
    val weight = instances.numInstances match {
      case 0 => 100
      case x => 100 / x
    }
    viewer.getTable().setHeaderVisible(true);
    viewer.getTable().setLinesVisible(true);
    for (i <- 0 until instances.numAttributes) {
      val a = instances.attribute(i)
      val viewerColumn = new TableViewerColumn(viewer, SWT.LEAD)
      layout.setColumnData(viewerColumn.getColumn, new ColumnWeightData(weight, 70, true))
      viewerColumn.getColumn.setText(a.name)
      viewerColumn.getColumn.setWidth(70)
      viewerColumn.getColumn.setResizable(true)
      viewerColumn.getColumn.setMoveable(true)
    }

    viewer.setLabelProvider(new InstanceLabelProvider);
    viewer.setContentProvider(new InstanceContentProvider);
    columnsInit = true;
    debug(this, "... columns created")
  }

  def configure(properties: Properties) = {
    debug(this, "Configure TablePresenter with " + properties)
    val row_string = properties.getProperty(TablePresenter.ROWS_PER_PAGE, "100")
    rows = row_string.toInt
  }

  def addListener(typ: Int, listener: Listener) = viewer.getTable.addListener(typ, listener)

  private def createInput(instances: Instances): Instances = {
    if (instances.numInstances > rows)
      new Instances(instances, 0, rows)
    else
      instances
  }

}

object TablePresenter {

  val name = "Table Presenter"
  val ROWS_PER_PAGE = "rows"
}

class TablePresenterFactory extends ProcessorFactory(classOf[TablePresenter]) {

  override val name: String = TablePresenter.name

  override def createDefaultProperties: Properties = {
    val properties = new Properties
    properties.setProperty(TablePresenter.ROWS_PER_PAGE, "100");
    properties
  }

  override def createPropertyValues: Map[String, Array[Any]] = Map(TablePresenter.ROWS_PER_PAGE -> Array(0, 1000))

  override def createPropertyDescription: Map[String, String] = Map(TablePresenter.ROWS_PER_PAGE -> "How much rows to show")
}
