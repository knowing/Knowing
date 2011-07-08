package de.lmu.ifi.dbs.knowing.core.internal

import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.weka._
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.{ Util, OSGIUtil }
import de.lmu.ifi.dbs.knowing.core.processing.TLoader
import de.lmu.ifi.dbs.knowing.core.validation.{ CrossValidatorFactory, XCrossValidatorFactory }
import java.util.Properties
import akka.actor.Actor
import org.osgi.framework.{ BundleActivator, BundleContext }

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
    osgiUtil.registerSaver(new WekaArffSaverFactory, WekaArffSaverFactory.id)
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

