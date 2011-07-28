package knowing.test

import de.lmu.ifi.dbs.knowing.core.provider.IDPUProvider
import de.lmu.ifi.dbs.knowing.core.provider.BundleDPUProvider
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import org.osgi.framework.BundleContext
import org.osgi.framework.BundleActivator
import knowing.test.loader._
import knowing.test.processor._
import knowing.test.filter._
import org.osgi.util.tracker.ServiceTrackerCustomizer
import org.osgi.framework.ServiceReference
import org.osgi.util.tracker.ServiceTracker
import org.osgi.framework.ServiceRegistration

class Activator extends BundleActivator {

  private var util: OSGIUtil = _
  private var dpuService: ServiceRegistration = _

  def start(context: BundleContext) = {
    Activator.context = context
    util = new OSGIUtil(context)
    util.registerProcessor(new TestLoaderFactory)
    util.registerProcessor(new SplitProcessorFactory)
    util.registerProcessor(new TestJavaProcessorFactory)
    util.registerProcessor(new SourceSplitFilterFactory)
    dpuService = context.registerService(classOf[IDPUProvider].getName, BundleDPUProvider.newInstance(context.getBundle), null)
    val dpus = OSGIUtil.registeredDPUs
    dpus foreach (dpu => println(dpu.name))
  }

  def stop(context: BundleContext) = {
    Activator.context = null
    util.unregisterAll
    util = null
    dpuService.unregister
  }

}

object Activator {

  private var context: BundleContext = null

  def getContext(): BundleContext = context
}

