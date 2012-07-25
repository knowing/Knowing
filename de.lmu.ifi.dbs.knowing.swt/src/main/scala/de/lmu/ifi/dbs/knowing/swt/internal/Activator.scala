package de.lmu.ifi.dbs.knowing.swt.internal

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
import de.lmu.ifi.dbs.knowing.core.swt._
import de.lmu.ifi.dbs.knowing.swt.wizard.SelectDPUPage
import org.osgi.util.tracker.ServiceTracker
import de.lmu.ifi.dbs.knowing.core.service._

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
class Activator extends AbstractUIPlugin {

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	override def start(context: BundleContext) = {
		super.start(context)
		Activator.plugin = this
		Activator.directoryTracker = new ServiceTracker(context, classOf[IFactoryDirectory], null)
		Activator.directoryTracker.open
		Activator.evaluateTracker = new ServiceTracker(context, classOf[IEvaluateService], null)
		Activator.evaluateTracker.open
		Activator.actorSystemTracker = new ServiceTracker(context, classOf[IActorSystemManager], null)
		Activator.actorSystemTracker.open

		loadDPUWizardProperties
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	override def stop(context: BundleContext) = {
		Activator.directoryTracker.close
		Activator.directoryTracker = null
		Activator.evaluateTracker.close
		Activator.evaluateTracker = null
		Activator.actorSystemTracker.close
		Activator.actorSystemTracker = null

		saveDPUWizardProperties
		Activator.plugin = null
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
	val PLUGIN_ID = "de.lmu.ifi.dbs.knowing.swt"; //$NON-NLS-1$

	// The shared instance
	private var plugin: Activator = _

	private var directoryTracker: ServiceTracker[IFactoryDirectory, IFactoryDirectory] = _
	private var evaluateTracker: ServiceTracker[IEvaluateService, IEvaluateService] = _
	private var actorSystemTracker: ServiceTracker[IActorSystemManager, IActorSystemManager] = _

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	def getDefault: Activator = plugin

	def directoryService: IFactoryDirectory = directoryTracker.getService
	def evaluateService: IEvaluateService = evaluateTracker.getService
	def actorSystemManager: IActorSystemManager = actorSystemTracker.getService
}
