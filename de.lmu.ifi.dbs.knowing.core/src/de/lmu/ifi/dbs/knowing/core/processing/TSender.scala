package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.events.Event
import scala.collection.immutable.Nil

trait TSender {

  val listeners = Nil
  
  def addListener(listener:ActorRef) = {
    listener :: listeners
  }
  
  def removeListener(listener:ActorRef) = {
    listeners - listener
  }
  
  def sendEvent(event:Event) =  listeners foreach (actor => sendToActor(actor,event))
  
  protected def sendToActor(actor:ActorRef, event:Event) = {
    if(!actor.isUnstarted && !actor.isShutdown)
    	actor ! event
  }
}