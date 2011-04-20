package de.lmu.ifi.dbs.knowing.core.internal

import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.weka._
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.Util
import de.lmu.ifi.dbs.knowing.core.test.Tester
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
//    logger info "Activator started"
    registerServices
    loadServices
    //    testActors
  }

  def stop(context: BundleContext) = {
    Activator.context = null;
  }

  private def testActors {
    val tester = Actor.actorOf(classOf[Tester]).start
    tester ! "do something"
  }

  private def registerServices {
    val arff = new WekaArffLoaderFactory()
//        Activator.context createService arff
    Activator.context.registerService(classOf[TFactory].getName(), arff, null)
  }

  private def loadServices {
    val arff = Util.getFactoryService(WekaArffLoaderFactory.id, null)
    val properties = new Properties()
    properties.setProperty(WekaArffLoader.PROP_FILE, "/home/muki/iris.arff")
    arff match {
      case Some(l) => l.getInstance.start ! Start(properties)
      case None => println("None found")
    }
    
    val testloader = Util.getFactoryService("Loader id", null);
    testloader match {
      case Some(l) => l.getInstance.start ! Start(properties)
      case None => println("None found")
    }
  }

}

object Activator {

  private var context: BundleContext = null

  def getContext(): BundleContext = context
}

