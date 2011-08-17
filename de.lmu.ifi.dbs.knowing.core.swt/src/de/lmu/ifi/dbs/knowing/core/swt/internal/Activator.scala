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
import org.eclipse.ui.plugin.AbstractUIPlugin
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import de.lmu.ifi.dbs.knowing.core.swt.wizard.SelectDPUPage
import de.lmu.ifi.dbs.knowing.core.swt._
import org.osgi.util.tracker.ServiceTracker
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
class Activator extends AbstractUIPlugin {

  private var osgi: OSGIUtil = _

  /**
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  override def start(context: BundleContext) = {
    super.start(context)
    Activator.plugin = this
    Activator.directoryTracker = new ServiceTracker(context, classOf[IFactoryDirectory].getName, null)
    Activator.directoryTracker.open
    osgi = new OSGIUtil(context)
    osgi.registerPresenter(new TablePresenterFactory)
    osgi.registerPresenter(new MultiTablePresenterFactory)
    loadDPUWizardProperties
  }

  /**
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
  override def stop(context: BundleContext) = {
    Activator.directoryTracker.close
    osgi.unregisterAll
    saveDPUWizardProperties
    Activator.plugin = null
    osgi = null
    Activator.directoryTracker = null
    super.stop(context)
  }

  private def saveDPUWizardProperties {
    val file = wizardPropertiesFile
    val writer = new PrintWriter(file)
    writer.println(SelectDPUPage.lastDPU)
    writer.println(SelectDPUPage.lastExecutionPath)
    writer.println(SelectDPUPage.fileSelected)
    writer.close
  }

  private def loadDPUWizardProperties {
    val file = wizardPropertiesFile
    try {
      val reader = new LineNumberReader(new InputStreamReader(new FileInputStream(file)))
      val dpuFile = reader.readLine
      if (dpuFile != null)
        SelectDPUPage.lastDPU = dpuFile
      val exePath = reader.readLine
      if (exePath != null)
        SelectDPUPage.lastExecutionPath = exePath
      val fileSelected = reader.readLine
      if (fileSelected != null)
        SelectDPUPage.fileSelected = fileSelected.toBoolean
      reader.close
    } catch {
      case e: IOException => println("No properties found")
    }
  }

  private def wizardPropertiesFile: File = {
    val stateLocation = getStateLocation
    val wizardProps = stateLocation.append("wizard")
    wizardProps.addFileExtension("properties")
    wizardProps.toFile
  }
}

object Activator {
  // The plug-in ID
  val PLUGIN_ID = "de.lmu.ifi.dbs.knowing.core.swt"; //$NON-NLS-1$

  // The shared instance
  var plugin: Activator = _

  var directoryTracker: ServiceTracker = _

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  def getDefault: Activator = plugin

  def directoryService: IFactoryDirectory = directoryTracker.getService.asInstanceOf[IFactoryDirectory]
}