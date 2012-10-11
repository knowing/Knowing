package de.lmu.ifi.dbs.knowing.core.swt.dialog

import org.eclipse.jface.dialogs.{ MessageDialog, Dialog }
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.{ Table, TableColumn, ProgressBar, TableItem, Shell, Control, Composite }
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.TableEditor
import scala.collection.mutable.Map
import akka.actor.{ ActorContext, ActorRef, ActorPath }
import de.lmu.ifi.dbs.knowing.core.events._
import org.eclipse.core.runtime.IStatus
import org.eclipse.jface.dialogs.ErrorDialog

class ProgressDialog(shell: Shell, var disposed: Boolean = false) extends Dialog(shell) {

  import ProgressDialog._

  var supervisorContext: ActorContext = _

  private var table: Table = _
  private var rows: Map[ActorPath, (TableItem, ProgressBar)] = Map()

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

  override protected def close: Boolean = {
    if (supervisorContext != null) {
      supervisorContext.stop(supervisorContext.self)
    }
    if (table != null)
      table.dispose()
    table = null
    rows = Map()
    supervisorContext = null
    disposed = true
    super.close
  }

  /**
   * Return the initial size of the dialog.
   */
  override protected def getInitialSize: Point = new Point(450, 300)

  /**
   * Handles different status updates. Stops process on finish or exception.
   *
   * @param actor - the actor sending the message, non null
   * @param status - the status being send, non null
   */
  def update(actor: ActorRef, status: Status) = {
    status match {

      //Actor created -> create ProgressBar
      case Created() =>
        val item = new TableItem(table, SWT.NONE)
        val name = actor.path.name
        item.setText(name)
        val bar = new ProgressBar(table, SWT.NONE)
        val editor = new TableEditor(table)
        editor.grabHorizontal = true
        editor.grabVertical = true
        editor.setEditor(bar, item, 2)
        rows += (actor.path -> (item, bar))
        changed(actor, 0, "Created")

      //Actor makes progress -> update ProgressBar linked with this actor
      case Progress(task, worked, work) =>
        val row = rows(actor.path)
        row._2.setMaximum(work)
        val msg = task match {
          case null | "" => "Progress"
          case task => task
        }
        changed(actor, worked, msg)

      //Should set ProgressBar to intermediate
      case Running() => changed(actor, 0, "Running")

      //Close ProgressDialog
      case Shutdown() => close

      //Handles exception thrown by actor
      case ExceptionEvent(e, details) =>
        val status = new org.eclipse.core.runtime.Status(IStatus.ERROR, "No plugin", details, e)
        ErrorDialog.openError(new Shell, "Error in " + actor.path.name, null, status)
        close

      case status => changed(actor, 0, status.toString)
    }
  }

  /**
   * Status for actor changed
   */
  private def changed(actor: ActorRef, selection: Int, msg: String) {
    val row = rows(actor.path)
    row._2.setSelection(selection)
    row._1.setText(1, msg)
  }

}

object ProgressDialog {
  val cols = Array(("Node", 150), ("Status", 100), ("Progress", 150))
}