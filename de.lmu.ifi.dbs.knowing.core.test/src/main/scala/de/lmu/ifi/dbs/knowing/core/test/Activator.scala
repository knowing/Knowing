package de.lmu.ifi.dbs.knowing.core.test

import de.lmu.ifi.dbs.knowing.core.service._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil

import de.lmu.ifi.dbs.knowing.core.test.loader._
import de.lmu.ifi.dbs.knowing.core.test.processor._
import de.lmu.ifi.dbs.knowing.core.test.filter._

import org.osgi.framework.{ BundleActivator, ServiceRegistration, BundleContext }

/**
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
class Activator extends BundleActivator {

  /** Demo how to register DPUs in a custom folder via a serviceProvider */
  private var provider: ServiceRegistration[IDPUProvider] = _

  /** Register your services programmatically via the OSGIUtil*/
  private var util: OSGIUtil = _

  def start(context: BundleContext) = {
    provider = BundleDPUProvider.newRegisteredInstance(context.getBundle)

    util = new OSGIUtil(context)
    util.registerProcessor(new TestLoaderFactory)
    util.registerProcessor(new EmptyLoaderFactory)
    util.registerProcessor(new SplitProcessorFactory)
    util.registerProcessor(new TestJavaProcessorFactory)
    util.registerProcessor(new SourceSplitFilterFactory)
    util.registerProcessor(new SerializableProcessorFactory)
    util.registerProcessor(new EmptyQueryProcessorFactory)
    util.registerProcessor(new ExceptionProcessorFactory)
    util.registerProcessor(new TestWekaFilterFactory)
    util.registerProcessor(new ClassPropertyProcessorFactory)
  }

  def stop(context: BundleContext) = {
    provider.unregister
    util.deregisterAll
    util = null
  }

}

object Activator {

  private var context: BundleContext = null

  def getContext(): BundleContext = context
}

