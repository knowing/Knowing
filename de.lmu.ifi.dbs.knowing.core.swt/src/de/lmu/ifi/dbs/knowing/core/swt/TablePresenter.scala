package de.lmu.ifi.dbs.knowing.core.swt

import java.util.{ LinkedList, Properties }
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.swt.provider.{ InstanceContentProvider, InstanceLabelProvider }
import de.lmu.ifi.dbs.knowing.presenter.{PresenterFactory,ITablePresenter}
import org.eclipse.jface.viewers.{ TableViewerColumn, TableViewer, LabelProvider, ColumnWeightData }
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.{ Composite, Button, Label, Spinner, Listener }
import org.eclipse.swt.layout.{ GridData, GridLayout }
import org.eclipse.swt.events.{ SelectionEvent, SelectionAdapter }
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.{ ArrayContentProvider, ITableLabelProvider }
import weka.core.{ Instances, Attribute }

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 22.04.2011
 *
 */
class TablePresenter extends SWTPresenter with ITablePresenter[Composite] {

  val name = "Table Presenter"

  private var viewer: TableViewer = _
  private var layout: TableColumnLayout = _
  private var columnsInit = false

  private var bLeft: Button = _
  private var bRight: Button = _

  private var model = new LinkedList[Array[String]]

  /**
   * @param attributes - contain header information
   */
  def buildTableHeader(attributes: Array[Attribute]) {
    val weight = attributes.length match {
      case 0 => 100
      case x => 100 / x
    }
    viewer.getTable.setHeaderVisible(true)
    viewer.getTable.setLinesVisible(true)
    for (a <- attributes) {
      val viewerColumn = new TableViewerColumn(viewer, SWT.LEAD)
      layout.setColumnData(viewerColumn.getColumn, new ColumnWeightData(weight, 70, true))
      viewerColumn.getColumn.setText(a.name)
      viewerColumn.getColumn.setWidth(70)
      viewerColumn.getColumn.setResizable(true)
      viewerColumn.getColumn.setMoveable(true)
    }

    //    viewer.setLabelProvider(new InstanceLabelProvider)
    viewer.setLabelProvider(new TablePresenterLabelProvider)
    viewer.setContentProvider(new ArrayContentProvider)
    viewer.setInput(model)
    log.debug("... columns created")
  }

  /**
   * @param content - string values containing column content
   */
  def addRow(content: Array[String]) = model.add(content)

  def update() = viewer.refresh()

  def createContainer(parent: Composite) {
    val composite = new Composite(parent, SWT.NONE)
    composite.setLayout(new GridLayout(4, false))

    bLeft = new Button(composite, SWT.NONE)
    val gd_bLeft = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1)
    gd_bLeft.widthHint = 40
    gd_bLeft.minimumWidth = 40
    gd_bLeft.horizontalIndent = -1
    gd_bLeft.verticalIndent = -1
    bLeft.setLayoutData(gd_bLeft)
    bLeft.setText("<-")
    bLeft.addSelectionListener(new SelectionAdapter {
      override def widgetSelected(event: SelectionEvent) {
        model.clear()
        previousPage()
        bRight.setEnabled(hasNextPage)
        bLeft.setEnabled(hasPreviousPage)
      }
    });

    val lPageIndex = new Label(composite, SWT.NONE)
    lPageIndex.setAlignment(SWT.CENTER)
    val gd_lPageIndex = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1)
    gd_lPageIndex.widthHint = 40
    lPageIndex.setLayoutData(gd_lPageIndex)
    lPageIndex.setText("- / -")

    bRight = new Button(composite, SWT.NONE)
    val gd_button = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1)
    gd_button.widthHint = 40
    bRight.setLayoutData(gd_button)
    bRight.setText("->")
    bRight.addSelectionListener(new SelectionAdapter {
      override def widgetSelected(event: SelectionEvent) {
        model.clear()
        nextPage()
        bRight.setEnabled(hasNextPage)
        bLeft.setEnabled(hasPreviousPage)
      }
    });

    val sRows = new Spinner(composite, SWT.BORDER)
    val gd_spinner = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1)
    gd_spinner.widthHint = 50
    sRows.setLayoutData(gd_spinner)
    sRows.setSelection(rowsPerPage)

    val tableComposite = new Composite(composite, SWT.NONE)
    layout = new TableColumnLayout
    tableComposite.setLayout(layout)
    tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1))
    viewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION)

    setParent(composite)
  }

  def createColumns(instances: Instances) {
    if (columnsInit)
      return

    log.debug("createColumns...")
    //TODO necessary
    instances.setClassIndex(-1); //WekaEnumeration skips the class attribute, so the class index has to be unset...
    val weight = instances.numInstances match {
      case 0 => 100
      case x => 100 / x
    }
    viewer.getTable().setHeaderVisible(true)
    viewer.getTable().setLinesVisible(true)
    for (i <- 0 until instances.numAttributes) {
      val a = instances.attribute(i)
      val viewerColumn = new TableViewerColumn(viewer, SWT.LEAD)
      layout.setColumnData(viewerColumn.getColumn, new ColumnWeightData(weight, 70, true))
      viewerColumn.getColumn.setText(a.name)
      viewerColumn.getColumn.setWidth(70)
      viewerColumn.getColumn.setResizable(true)
      viewerColumn.getColumn.setMoveable(true)
    }

    viewer.setLabelProvider(new InstanceLabelProvider)
    viewer.setContentProvider(new InstanceContentProvider)
    columnsInit = true
    log.debug("... columns created")
  }

  def addListener(typ: Int, listener: Listener) = viewer.getTable.addListener(typ, listener)

}

class TablePresenterFactory extends PresenterFactory(classOf[TablePresenter], classOf[ITablePresenter[Composite]]) {

  override def createDefaultProperties: Properties = {
    val properties = new Properties
    properties.setProperty(ITablePresenter.ROWS_PER_PAGE, "100");
    properties
  }

  override def createPropertyValues: Map[String, Array[Any]] = Map(ITablePresenter.ROWS_PER_PAGE -> Array(0, 1000))

  override def createPropertyDescription: Map[String, String] = Map(ITablePresenter.ROWS_PER_PAGE -> "How much rows to show")
}

class TablePresenterLabelProvider extends LabelProvider with ITableLabelProvider {

  def getColumnImage(element: Object, columnIndex: Int): Image = null

  def getColumnText(element: Object, columnIndex: Int): String = element.asInstanceOf[Array[String]](columnIndex)

}