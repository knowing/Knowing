package de.lmu.ifi.dbs.knowing.core.swt

import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.{ Display, Control, Composite, Label, Listener }
import weka.core.Instances
import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import de.lmu.ifi.dbs.knowing.core.swt.listener.SWTListener
import scala.collection.mutable.ArrayBuffer

/**
 * Base class for writing TPresenters for the
 * Standard Widget Toolkit (SWT).
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 22.04.2011
 *
 */
abstract class SWTPresenter extends TPresenter[Composite] {

  protected var parent: Composite = _
  protected val containers = ArrayBuffer[Composite]()

  override def presenterReceive = {
    case SWTListener(typ, listener) =>
      log.debug("Received SWTListener: " + listener)
      sync(parent) { addListener(typ, listener) }
  }

  /**
   * Clients must implement this method in order
   * to register listener on their components.
   */
  def addListener(typ: Int, listener: Listener)

  /**
   * Executes the given function on the UI Thread synchronous.
   *
   * @param parent - used to access display
   * @param syncFun - function which will be executed on the UI thread
   */
  def sync(c: Composite)(syncFun: => Unit) {
    c match {
      case null =>
        log.warning("Composite to sync on is null => [" + c + "]. Trying Display.getDefault()")
        Display.getDefault.syncExec(new Runnable {
          def run = syncFun
        })
      case _ =>
        c.getDisplay.syncExec(new Runnable {
          def run = syncFun
        })
    }

  }

  /**
   * Executes the given runnable on the UI Thread synchronous.
   *
   * @param parent - used to access display
   * @param runnable - runnable which will be executed on the UI thread
   */
  def sync(parent: Composite, runnable: Runnable) = parent.getDisplay.syncExec(runnable)

  /**
   *
   */
  def dispose = sync(parent) { containers foreach (_.dispose) }

  /**
   *
   */
  def redraw = sync(parent) { containers foreach (_.redraw) }

  /**
   *
   */
  def getContainerClass(): String = classOf[Composite].getName

  /**
   *
   */
  def getParent(): Composite = parent

  /**
   * Clients must set the parent class variable
   * in order to get properly sync with the UI Thread.
   */
  def setParent(parent: Composite) = this.parent = parent

}

object SWTPresenter {
  val TABLE_PRESENTER = classOf[TablePresenter].getName
}
