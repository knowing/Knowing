/**
 *
 */
package de.lmu.ifi.dbs.knowing.core.swt.internal

import org.osgi.framework.ServiceRegistration
import de.lmu.ifi.dbs.knowing.core.swt._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import org.osgi.framework.BundleContext
import org.eclipse.ui.plugin.AbstractUIPlugin

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
class Activator extends AbstractUIPlugin {
  
  var services:List[ServiceRegistration] = Nil

  /**
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  override def start(context: BundleContext) = {
    super.start(context);
    Activator.plugin = this;
    services = context.registerService(classOf[TFactory].getName, new TablePresenterFactory, null) :: services
    services = context.registerService(classOf[TFactory].getName, new MultiTablePresenterFactory, null) :: services
  }

  /**
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
  override def stop(context: BundleContext) = {
    services foreach (registration => registration.unregister)
    Activator.plugin = null;
    super.stop(context);
  }
}

object Activator {
  // The plug-in ID
  val PLUGIN_ID = "de.lmu.ifi.dbs.knowing.core.swt"; //$NON-NLS-1$

  // The shared instance
  var plugin: Activator = _;

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  def getDefault: Activator = plugin
}