/*																*\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|	**
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,	**
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|	**
** 																**
** Knowing Framework											**
** Apache License - http://www.apache.org/licenses/				**
** LMU Munich - Database Systems Group							**
** http://www.dbs.ifi.lmu.de/									**
\*																*/
package de.lmu.ifi.dbs.knowing.core.internal

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceReference
import org.osgi.framework.ServiceRegistration
import org.osgi.util.tracker.ServiceTracker
import org.osgi.util.tracker.ServiceTrackerCustomizer
import de.lmu.ifi.dbs.knowing.core.service._
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import de.lmu.ifi.dbs.knowing.core.validation._
import de.lmu.ifi.dbs.knowing.core.weka._
import Activator._

class Activator extends BundleActivator {

	def start(context: BundleContext) = {
		Activator.context = context
		osgiUtil = new OSGIUtil(context)
		registerServices

		dpuDirectory = new ServiceTracker[IDPUDirectory, IDPUDirectory](context, classOf[IDPUDirectory], null)
		dpuDirectory.open

		factoryDirectory = new ServiceTracker[IFactoryDirectory, IFactoryDirectory](context, classOf[IFactoryDirectory], null)
		factoryDirectory.open
	}

	def stop(context: BundleContext) = {
		Activator.context = null
		osgiUtil.deregisterAll
		osgiUtil = null

		dpuDirectory.close
		dpuDirectory = null

		factoryDirectory.close
		factoryDirectory = null
	}

	private def registerServices {
		osgiUtil.registerLoader(new ExtendedWekaArffLoaderFactory, ExtendedWekaArffLoaderFactory.id)
		osgiUtil.registerLoader(new WekaArffLoaderFactory, WekaArffLoaderFactory.id)
		osgiUtil.registerSaver(new WekaArffSaverFactory, WekaArffSaverFactory.id)
		
		//deprecated
		osgiUtil.registerProcessor(new OneRFactory, classOf[weka.classifiers.rules.OneR].getName)
		
		osgiUtil.registerProcessor(new CrossValidatorFactory, CrossValidatorFactory.id)
		osgiUtil.registerProcessor(new XCrossValidatorFactory, XCrossValidatorFactory.id)
		osgiUtil.registerProcessor(new AttributeCrossValidatorFactory)
		osgiUtil.registerProcessor(new ConfusionMatrixFactory)
	}

}

object Activator {

	val PLUGIN_ID = "de.lmu.ifi.dbs.knowing.core"

	private var context: BundleContext = null
	private var osgiUtil: OSGIUtil = _

	var providerTracker: ServiceTracker[IDPUProvider, IDPUProvider] = _
	var dpuDirectory: ServiceTracker[IDPUDirectory, IDPUDirectory] = _
	var factoryDirectory: ServiceTracker[IFactoryDirectory, IFactoryDirectory] = _

	def getContext(): BundleContext = context
}

