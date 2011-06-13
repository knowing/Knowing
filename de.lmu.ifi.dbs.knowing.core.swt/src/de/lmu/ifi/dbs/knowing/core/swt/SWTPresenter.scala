package de.lmu.ifi.dbs.knowing.core.swt

import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.{ Composite, Label, Listener }
import weka.core.Instances
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import de.lmu.ifi.dbs.knowing.core.swt.handler.SWTListener

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
abstract class SWTPresenter extends TPresenter[Composite] {

  private var composite: Composite = _
  
  override def customReceive = {
    case SWTListener(typ, listener) => composite.getDisplay.asyncExec(new Runnable() {
      def run = addListener(typ, listener) })
    
  }

  def createContainer(parent: Composite) {
    if (composite != null && !composite.isDisposed())
      dispose

    parent.getDisplay().syncExec(new Runnable {
      def run {
        composite = new Composite(parent, SWT.NONE)
        composite.setLayout(new FillLayout)
        createControl(composite)
      }
    })
  }

  def dispose = composite dispose

  def redraw = composite redraw
  
  def addListener(typ:Int, listener:Listener)

  /**
   * <p>Delegates the call to buildContent() and runs it sync <br>
   * in the UI thread to avoid invalid thread access.</p>
   * @param instances
   */
  def buildPresentation(instances: Instances) {
    if (composite != null) {
      composite.getDisplay().syncExec(new Runnable {
        def run {
          debug(this, "SWTPresenter.buildPresentation with  " + instances.relationName)
          buildContent(instances);
        }
      })
    }
  }

  def getContainerClass(): String = classOf[Composite].getName

  /**
   *
   */
  def createControl(parent: Composite)

  /**
   *
   */
  def buildContent(instances: Instances)

}
