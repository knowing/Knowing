package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.{ Actor, ActorRef }
import akka.event.EventHandler.{ debug, info, warning, error }
import com.eaio.uuid.UUID
import scala.collection.mutable.Map
import de.lmu.ifi.dbs.knowing.core.events._

/**
 * <p>This actor is able to send Events to registered actors</p>
 */
trait TSender { this: Actor =>

  val listeners = Map.empty[UUID, ActorRef]
  val outputListeners = Map.empty[String, Map[UUID, ActorRef]]

  def addListener(listener: ActorRef, port: Option[String]) {
    port match {
      case Some(p) => addListener(listener, p)
      case None => addListener(listener)
    }
  }

  def removeListener(listener: ActorRef, port: Option[String]) {
    port match {
      case Some(p) => removeListener(listener, p)
      case None => removeListener(listener)
    }
  }

  /* ========================== */
  /* == Generic input/output == */
  /* ========================== */

  def addListener(listener: ActorRef) {
    listeners += (listener.getUuid -> listener)
    if (self.getSender.isDefined)
      self reply Registered(true)
  }

  def removeListener(listener: ActorRef) = listeners remove (listener.getUuid)

  protected def sendEvent(event: Event) = listeners foreach { case (_, actor) => sendToActor(actor, event) }

  /* ========================= */
  /* == Custom input/output == */
  /* ========================= */

  protected def addListener(listener: ActorRef, port: String) {
    val entry = outputListeners.get(port)
    val value = (listener.getUuid -> listener)
    entry match {
      case Some(e) => e += value
      case None => outputListeners += (port -> Map(value))
    }
    if (self.getSender.isDefined)
      self reply Registered(true)
  }

  protected def removeListener(listener: ActorRef, port: String) {
    val entry = outputListeners.get(port)
    entry match {
      case Some(e) => e.remove(listener.getUuid)
      case None => warning(this, "Listener " + listener + " could not be removed")
    }
  }

  protected def sendEvent(event: Event, output: String) {
    val entry = outputListeners.get(output)
    entry match {
      case Some(e) => e foreach { case (_, actor) => sendToActor(actor, event) }
      case None => warning(this, "Event " + event + " could not be send")
    }
  }

  protected def sendToActor(actor: ActorRef, event: Event) {
    if (actor.isRunning)
      actor ! event
  }
}