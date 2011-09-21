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
    tracker = new ServiceTracker(context, classOf[IDPUProvider], new DPUProviderServiceTracker(context))
    tracker.open
  }

  def stop(context: BundleContext) = {
    Activator.context = null;
    osgiUtil.deregisterAll
    osgiUtil = null
    tracker.close
  }

  private def registerServices {
    osgiUtil.registerLoader(new ExtendedWekaArffLoaderFactory, ExtendedWekaArffLoaderFactory.id)
    osgiUtil.registerLoader(new WekaArffLoaderFactory, WekaArffLoaderFactory.id)
    osgiUtil.registerSaver(new WekaArffSaverFactory, WekaArffSaverFactory.id)
    osgiUtil.registerProcessor(new NaiveBayesFactory, classOf[weka.classifiers.bayes.NaiveBayes].getName)
    osgiUtil.registerProcessor(new OneRFactory, classOf[weka.classifiers.rules.OneR].getName)
    osgiUtil.registerProcessor(new CrossValidatorFactory, CrossValidatorFactory.id)
    osgiUtil.registerProcessor(new XCrossValidatorFactory, XCrossValidatorFactory.id)
    osgiUtil.registerProcessor(new AttributeCrossValidatorFactory)
    osgiUtil.registerProcessor(new ConfusionMatrixFactory)
  }

}

object Activator {

  private var context: BundleContext = null
  private var osgiUtil: OSGIUtil = _
  
  var tracker: ServiceTracker[IDPUProvider,IDPUProvider] = _

  def getContext(): BundleContext = context
}

class DPUProviderServiceTracker(context: BundleContext) extends ServiceTrackerCustomizer[IDPUProvider,IDPUProvider] {

  def addingService(reference: ServiceReference[IDPUProvider]): IDPUProvider = context.getService(reference)

  def modifiedService(reference: ServiceReference[IDPUProvider], service: IDPUProvider) {
    service.getDataProcessingUnits foreach (dpu => println("# " + dpu.getName.getContent))
  }

  def removedService(reference: ServiceReference[IDPUProvider], service: IDPUProvider) {

  }
}