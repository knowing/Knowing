package de.lmu.ifi.dbs.knowing.core.internal

import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.weka._
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.Util
import de.lmu.ifi.dbs.knowing.core.processing.TLoader

import java.util.Properties

//import com.weiglewilczek.scalamodules._
//import com.weiglewilczek.slf4s._

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
    val arff = new WekaArffLoaderFactory()
    val naiveBayes = new NaiveBayesFactory()
    val oneR = new OneRFactory()
    //        Activator.context createService arff
    Activator.context.registerService(classOf[TFactory].getName(), arff, null)
    Activator.context.registerService(classOf[TFactory].getName(), naiveBayes, null)
    Activator.context.registerService(classOf[TFactory].getName(), oneR, null)
  }

}

object Activator {

  private var context: BundleContext = null

  def getContext(): BundleContext = context
}

