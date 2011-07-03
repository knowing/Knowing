package de.lmu.ifi.dbs.knowing.core.internal

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import de.lmu.ifi.dbs.knowing.core.validation.CrossValidatorFactory
import de.lmu.ifi.dbs.knowing.core.validation.XCrossValidatorFactory
import de.lmu.ifi.dbs.knowing.core.weka.NaiveBayesFactory
import de.lmu.ifi.dbs.knowing.core.weka.OneRFactory
import de.lmu.ifi.dbs.knowing.core.weka.WekaArffLoaderFactory

class Activator extends BundleActivator {

  private var osgiUtil: OSGIUtil = _

  def start(context: BundleContext) = {
    Activator.context = context
    osgiUtil = new OSGIUtil(context)
    registerServices
  }

  def stop(context: BundleContext) = {
    Activator.context = null;
    osgiUtil.deregisterAll
    osgiUtil = null
  }

  private def registerServices {
    osgiUtil.registerLoader(new WekaArffLoaderFactory, WekaArffLoaderFactory.id)
    osgiUtil.registerProcessor(new NaiveBayesFactory, classOf[weka.classifiers.bayes.NaiveBayes].getName)
    osgiUtil.registerProcessor(new OneRFactory, classOf[weka.classifiers.rules.OneR].getName)
    osgiUtil.registerProcessor(new CrossValidatorFactory, CrossValidatorFactory.id)
    osgiUtil.registerProcessor(new XCrossValidatorFactory, XCrossValidatorFactory.id)
  }

}

object Activator {

  private var context: BundleContext = null

  def getContext(): BundleContext = context
}

