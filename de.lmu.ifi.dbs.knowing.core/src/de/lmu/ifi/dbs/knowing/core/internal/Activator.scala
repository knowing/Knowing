package de.lmu.ifi.dbs.knowing.core.internal

import akka.actor.Actor
import de.lmu.ifi.dbs.knowing.core.test.Tester
import org.osgi.framework.{ BundleActivator, BundleContext }

class Activator extends BundleActivator  {
    
  def start(context: BundleContext) = {  
    Activator.context = context
    println("Activator started")
    val tester = Actor.actorOf(classOf[Tester]).start
    tester ! "do something"
  }

  def stop(context: BundleContext) = {  
    Activator.context = null;
  }

}

object Activator  { 
  
  private var context:BundleContext = null
  
  def getContext():BundleContext = context
}

