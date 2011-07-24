package de.lmu.ifi.dbs.knowing.core.swt.dialog

import org.eclipse.jface.dialogs.Dialog
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Table
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.TableColumn
import org.eclipse.swt.widgets.ProgressBar
import de.lmu.ifi.dbs.knowing.core.events._
import scala.collection.mutable.Map
import akka.actor.ActorRef
import org.eclipse.swt.widgets.TableItem
import org.eclipse.swt.custom.TableEditor
import com.eaio.uuid.UUID

class ProgressDialog(shell: Shell, var disposed:Boolean = false) extends Dialog(shell) {

  import ProgressDialog._

  private var table: Table = _
  private val rows: Map[UUID, (TableItem, ProgressBar)] = Map()
  
  setBlockOnOpen(false)

  /**
   * Create contents of the dialog.
   * @param parent
   */
  override protected def createDialogArea(parent: Composite): Control = {
    val container = super.createDialogArea(parent).asInstanceOf[Composite]
    container.setLayout(new FillLayout(SWT.HORIZONTAL))

    /* == Generate ProgressTabel == */
    table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION)
    table.setHeaderVisible(true)
    table.setLinesVisible(true)
    cols foreach {
      case (name, width) =>
        val col = new TableColumn(table, SWT.NONE)
        col.setText(name)
        col.setWidth(width)
    }
    
    return container;
  }

  /**
   * Create contents of the button bar.
   * @param parent
   */
  override protected def createButtonsForButtonBar(parent: Composite) {
    //		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    //		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }
  
  override protected def close:Boolean = {
    disposed = true
    super.close
  }

  /**
   * Return the initial size of the dialog.
   */
  override protected def getInitialSize: Point = new Point(450, 300)

  def update(actor: ActorRef, status: Status) = {
    status match {
      case Created() =>
        val item = new TableItem(table, SWT.NONE)
        val name = actor.getActorClassName.split('.').last
        item.setText(name)
        item.setText(1, "Created")
        val bar = new ProgressBar(table, SWT.NONE)
        val editor = new TableEditor(table)
        editor.grabHorizontal = true
        editor.grabVertical = true
        editor.setEditor(bar, item, 2)
        rows += (actor.getUuid -> (item, bar))
      case Progress(task, worked, work) =>
        val row = rows(actor.getUuid)
        row._2.setMaximum(work)
        row._2.setSelection(row._2.getSelection + worked)
        row._1.setText(1, "Progress")
      case Running() =>
        val row = rows(actor.getUuid)
        row._2.setSelection(0)
        row._1.setText(1, "Running")
      case Shutdown() => close
      case status =>
        val row = rows(actor.getUuid)
        row._2.setSelection(0)
        row._1.setText(1, status.toString)
    }
  }

}

object ProgressDialog {
  val cols = Array(("Node", 150), ("Status", 100), ("Progress", 150))
}