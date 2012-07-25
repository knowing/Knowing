/**
 *
 */
package de.lmu.ifi.dbs.knowing.core.swt.internal

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.io.PrintWriter
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import de.lmu.ifi.dbs.knowing.core.swt._
import org.osgi.util.tracker.ServiceTracker
import org.osgi.framework.BundleActivator
import de.lmu.ifi.dbs.knowing.core.service._


/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
class Activator extends BundleActivator {

	private var osgi: OSGIUtil = _

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	override def start(context: BundleContext) = {
		Activator.plugin = this

		osgi = new OSGIUtil(context)
		osgi.registerPresenter(new TablePresenterFactory)
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	override def stop(context: BundleContext) = {
		osgi.deregisterAll
		osgi = null
		Activator.plugin = null
	}

}

object Activator {
	// The plug-in ID
	val PLUGIN_ID = "de.lmu.ifi.dbs.knowing.core.swt"; //$NON-NLS-1$

	// The shared instance
	private var plugin: Activator = _

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	def getDefault: Activator = plugin
}
