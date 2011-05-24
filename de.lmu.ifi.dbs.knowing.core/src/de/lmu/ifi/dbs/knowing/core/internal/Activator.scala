package de.lmu.ifi.dbs.knowing.core.internal

import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.weka._
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.Util
import de.lmu.ifi.dbs.knowing.core.processing.TLoader
import de.lmu.ifi.dbs.knowing.core.validation.{CrossValidatorFactory, XCrossValidatorFactory }
import java.util.Properties
import akka.actor.Actor
import org.osgi.framework.{ BundleActivator, BundleContext }


class Activator extends BundleActivator {
  //with Logging

  def start(context: BundleContext) = {
    Activator.context = context
    println("Activator started")
    //logger info "Activator started"
    registerServices
  }

  def stop(context: BundleContext) = {
    Activator.context = null;
  }

  private def registerServices {
    val arff = new WekaArffLoaderFactory
    val naiveBayes = new NaiveBayesFactory
    val oneR = new OneRFactory
    val crossVal = new CrossValidatorFactory
    val xcrossVal = new XCrossValidatorFactory
    //        Activator.context createService arff
    Activator.context.registerService(classOf[TFactory].getName(), arff, null)
    Activator.context.registerService(classOf[TFactory].getName(), naiveBayes, null)
    Activator.context.registerService(classOf[TFactory].getName(), oneR, null)
    Activator.context.registerService(classOf[TFactory].getName(), crossVal, null)
    Activator.context.registerService(classOf[TFactory].getName(), xcrossVal, null)
  }

}

object Activator {

  private var context: BundleContext = null

  def getContext(): BundleContext = context
}

