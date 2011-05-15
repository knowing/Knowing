package de.lmu.ifi.dbs.knowing.core.swt

import de.lmu.ifi.dbs.knowing.core.events.UIContainer
import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import weka.core.Instances
import akka.event.EventHandler

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
abstract class SWTPresenter extends TPresenter[Composite] {

  private var composite: Composite = _

  def createContainer(parent: Composite) = {
    if (composite != null && !composite.isDisposed())
      dispose;

    parent.getDisplay().syncExec(new Runnable() {
      def run() {
        EventHandler.debug(this,"SWTPresenter.createContainer with " + parent)
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout());
        createControl(composite);
      }
    });
  }

  def dispose = composite dispose
  
  def redraw = composite redraw

  /**
   * <p>Delegates the call to buildContent() and runs it async <br>
   * in the UI thread to avoid invalid thread access.</p>
   * @param instances
   */
  def buildPresentation(instances: Instances) = {
    if (composite != null) {
      composite.getDisplay().syncExec(new Runnable() {
        def run() {
          EventHandler.debug(this,"SWTPresenter.buildPresentation with  " + instances.relationName)
          buildContent(instances);
        }
      });
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
