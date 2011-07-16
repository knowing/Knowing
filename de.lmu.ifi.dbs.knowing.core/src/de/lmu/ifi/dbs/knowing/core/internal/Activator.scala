package de.lmu.ifi.dbs.knowing.core.internal

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceReference
import org.osgi.framework.ServiceRegistration
import org.osgi.util.tracker.ServiceTracker
import org.osgi.util.tracker.ServiceTrackerCustomizer

import de.lmu.ifi.dbs.knowing.core.provider.BundleDPUProvider
import de.lmu.ifi.dbs.knowing.core.provider.IDPUProvider
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import de.lmu.ifi.dbs.knowing.core.validation.CrossValidatorFactory
import de.lmu.ifi.dbs.knowing.core.validation.XCrossValidatorFactory
import de.lmu.ifi.dbs.knowing.core.weka.NaiveBayesFactory
import de.lmu.ifi.dbs.knowing.core.weka.OneRFactory
import de.lmu.ifi.dbs.knowing.core.weka.WekaArffLoaderFactory
import de.lmu.ifi.dbs.knowing.core.weka.WekaArffSaverFactory

import Activator._

class Activator extends BundleActivator {

  def start(context: BundleContext) = {
    Activator.context = context
    osgiUtil = new OSGIUtil(context)
    registerServices
    tracker = new ServiceTracker(context, classOf[IDPUProvider].getName, new DPUProviderServiceTracker(context))
    tracker.open

  }

  def stop(context: BundleContext) = {
    Activator.context = null;
    osgiUtil.deregisterAll
    osgiUtil = null
    tracker.close
  }

  private def registerServices {
    osgiUtil.registerLoader(new WekaArffLoaderFactory, WekaArffLoaderFactory.id)
    osgiUtil.registerSaver(new WekaArffSaverFactory, WekaArffSaverFactory.id)
    osgiUtil.registerProcessor(new NaiveBayesFactory, classOf[weka.classifiers.bayes.NaiveBayes].getName)
    osgiUtil.registerProcessor(new OneRFactory, classOf[weka.classifiers.rules.OneR].getName)
    osgiUtil.registerProcessor(new CrossValidatorFactory, CrossValidatorFactory.id)
    osgiUtil.registerProcessor(new XCrossValidatorFactory, XCrossValidatorFactory.id)
  }

}

object Activator {

  private var context: BundleContext = null
  private var osgiUtil: OSGIUtil = _
  
  var tracker: ServiceTracker = _

  def getContext(): BundleContext = context
}

class DPUProviderServiceTracker(context: BundleContext) extends ServiceTrackerCustomizer {

  def addingService(reference: ServiceReference): Object = {
    val service = context.getService(reference).asInstanceOf[IDPUProvider]
    service
  }

  def modifiedService(reference: ServiceReference, service: Object) {
    service.asInstanceOf[IDPUProvider].getDataProcessingUnits foreach (dpu => println("# " + dpu.name))
  }

  def removedService(reference: ServiceReference, service: Object) {

  }
}