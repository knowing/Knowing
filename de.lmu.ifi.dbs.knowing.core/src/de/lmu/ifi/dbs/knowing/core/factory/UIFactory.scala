package de.lmu.ifi.dbs.knowing.core.factory

import akka.actor.Actor
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.graph.Node
import de.lmu.ifi.dbs.knowing.core.events.Status

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 22.04.2011
 *
 */
trait UIFactory { 
  
  def createContainer(node: Node):AnyRef
  
  def update(actor: ActorRef, status: Status)
  
  def setSupervisor(supervisor: ActorRef)
  
}