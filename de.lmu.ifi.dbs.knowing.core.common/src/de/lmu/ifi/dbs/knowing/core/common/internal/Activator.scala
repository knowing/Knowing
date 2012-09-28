package de.lmu.ifi.dbs.knowing.core.common.internal

import org.osgi.framework.{BundleActivator,BundleContext}
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import de.lmu.ifi.dbs.knowing.core.common._
import de.lmu.ifi.dbs.knowing.core.common.io._

/**
 * @author Nepomuk Seiler
 * 
 */
class Activator extends BundleActivator {

  private var osgiUtil: OSGIUtil = _
  
  def start(context: BundleContext) = {
     osgiUtil = new OSGIUtil(context)
     osgiUtil.registerProcessor(new AddClassAttributeFactory)
     osgiUtil.registerSaver(new InstancesSaverFactory)
     osgiUtil.registerLoader(new InstancesLoaderFactory)
     osgiUtil.registerProcessor(new UncertainClassDumperFactory)
  }

  def stop(context: BundleContext) = {
    osgiUtil.deregisterAll
    osgiUtil = null
  }

}