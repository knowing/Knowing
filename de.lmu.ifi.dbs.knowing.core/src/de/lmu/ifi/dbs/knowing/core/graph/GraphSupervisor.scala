package de.lmu.ifi.dbs.knowing.core.graph

import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util._
import de.lmu.ifi.dbs.knowing.core.processing.{IPresenter, TSender}
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.events._
import akka.actor.{ Actor, ActorRef }
import akka.actor.Actor.actorOf

import org.osgi.framework.FrameworkUtil

class GraphSupervisor(val dpu: DataProcessingUnit) extends Actor with TSender {

  var actors:List[ActorRef] = Nil
  
  def receive = {
    case Register => addListener(self)
    case event: Event => log.info("Event occured: " + event)
    case _ => log.error("Unkown message")
  }

  def evaluate() = {
	actors foreach (actor => startAndRegister(actor))
  }
  
  private def startAndRegister(actor:ActorRef) = {
    actor.start
    actor ! Register
  }

  def initialize() = {
    val nodes = dpu.nodes
    for (i <- 0 to nodes.size) {
      val node = nodes(i)
      val id = node.id
      val loader = Util.getFactoryService("Loader id", null)
      loader match {
        case None => log.error("Loader id" + "loader not found")
        case Some(l) => l.getInstance :: actors
      }
    }
  }

  def getPresenterActors(): List[IPresenter[Unit]] = {
    Nil
  }

}