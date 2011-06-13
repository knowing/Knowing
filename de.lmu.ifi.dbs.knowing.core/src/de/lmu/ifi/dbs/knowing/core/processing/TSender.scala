package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.Actor
import akka.actor.ActorRef
import com.eaio.uuid.UUID
import de.lmu.ifi.dbs.knowing.core.events._

trait TSender { this: Actor =>

  val listeners = collection.mutable.Map.empty[UUID, ActorRef]

  def addListener(listener: ActorRef) = {
    listeners += (listener.getUuid -> listener)
    if (self.getSender.isDefined)
      self reply Registered(true)
  }

  def removeListener(listener: ActorRef) = listeners remove (listener.getUuid)

  def sendEvent(event: Event) = listeners foreach { case (_, actor) => sendToActor(actor, event) }

  protected def sendToActor(actor: ActorRef, event: Event) = {
    if (actor.isRunning)
      actor ! event
  }
}