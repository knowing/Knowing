/*                                                               *\
 ** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
 ** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
 ** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
 **                                                              **
 ** Knowing Framework                                            **
 ** Apache License - http://www.apache.org/licenses/             **
 ** LMU Munich - Database Systems Group                          **
 ** http://www.dbs.ifi.lmu.de/                                   **
\*                                                               */
package de.lmu.ifi.dbs.knowing.weka.internal

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceReference
import org.osgi.framework.ServiceRegistration
import org.osgi.util.tracker.ServiceTracker
import org.osgi.util.tracker.ServiceTrackerCustomizer
import de.lmu.ifi.dbs.knowing.core.service._
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import de.lmu.ifi.dbs.knowing.core.validation._
import de.lmu.ifi.dbs.knowing.weka.io._
import de.lmu.ifi.dbs.knowing.weka.classifier._
import de.lmu.ifi.dbs.knowing.weka.clusterer._
import Activator._

class Activator extends BundleActivator {

  def start(context: BundleContext) = {
    Activator.context = context
    osgiUtil = new OSGIUtil(context)
    registerServices

  }

  def stop(context: BundleContext) = {
    Activator.context = null
    osgiUtil.deregisterAll
    osgiUtil = null

  }

  private def registerServices {
    osgiUtil.registerLoader(new ExtendedWekaArffLoaderFactory, ExtendedWekaArffLoaderFactory.id)
    osgiUtil.registerLoader(new WekaArffLoaderFactory)
    osgiUtil.registerSaver(new WekaArffSaverFactory)
    osgiUtil.registerProcessor(new WekaNaiveBayesFactory)
    osgiUtil.registerProcessor(new WekaBayesNetFactory)
    osgiUtil.registerProcessor(new WekaSimpleKMeansFactory)
  }

}

object Activator {

  val PLUGIN_ID = "de.lmu.ifi.dbs.knowing.weka"

  private var context: BundleContext = null
  private var osgiUtil: OSGIUtil = _

  def getContext(): BundleContext = context
}

