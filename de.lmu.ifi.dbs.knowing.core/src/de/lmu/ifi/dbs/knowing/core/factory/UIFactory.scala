package de.lmu.ifi.dbs.knowing.core.factory

import akka.actor.Actor
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.events.Status
import de.lmu.ifi.dbs.knowing.core.model.INode

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
trait UIFactory[T] { 
  
  def createContainer(node: INode):T
  
  def update(actor: ActorRef, status: Status)
  
  def setSupervisor(supervisor: ActorRef)
  
}