package knowing.test


import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import org.osgi.framework.BundleContext
import org.osgi.framework.BundleActivator
import knowing.test.loader._
import knowing.test.processor._


class Activator extends BundleActivator {

  private var util:OSGIUtil = _
  
  def start(context: BundleContext) = {
    Activator.context = context
    util = new OSGIUtil(context)
    util.registerProcessor(new TestLoaderFactory)
    util.registerProcessor(new SplitProcessorFactory)
    util.registerProcessor(new TestJavaProcessorFactory)
  }

  def stop(context: BundleContext) = {
    Activator.context = null
    util.deregisterAll
    util = null
  }

}

object Activator {

  private var context: BundleContext = null

  def getContext(): BundleContext = context
}