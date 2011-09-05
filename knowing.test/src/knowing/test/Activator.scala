package knowing.test

import de.lmu.ifi.dbs.knowing.core.service._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil

import knowing.test.loader._
import knowing.test.processor._
import knowing.test.filter._

import org.osgi.framework.ServiceReference
import org.osgi.framework.ServiceRegistration
import org.osgi.framework.BundleContext
import org.osgi.framework.BundleActivator
import org.osgi.util.tracker.ServiceTrackerCustomizer
import org.osgi.util.tracker.ServiceTracker

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
    util.registerProcessor(new SerializableProcessorFactory)
    dpuService = context.registerService(classOf[IDPUProvider].getName, BundleDPUProvider.newInstance(context.getBundle), null)
    val dpus = OSGIUtil.registeredDPUs
    dpus foreach (dpu => println(dpu.getName.getContent))
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

