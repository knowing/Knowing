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

  def start(context: BundleContext) = {
  }

  def stop(context: BundleContext) = {
  }

}

object Activator {

  private var context: BundleContext = null

  def getContext(): BundleContext = context
}

