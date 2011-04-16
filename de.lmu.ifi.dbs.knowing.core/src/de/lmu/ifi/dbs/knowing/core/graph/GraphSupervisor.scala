package de.lmu.ifi.dbs.knowing.core.graph

import de.lmu.ifi.dbs.knowing.core.factory.TLoaderFactory
import de.lmu.ifi.dbs.knowing.core.util._
import de.lmu.ifi.dbs.knowing.core.processing.IPresenter
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.processing.TSender
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
    val nodes = dpu.getNodes
    for (i <- 0 to nodes.size) {
      val node = nodes.get(i)
      val id = node.getFactoryId
      val loader = Util.getLoaderService("Loader id", null)
      loader match {
        case None => log.error("Loader id" + "loader not found")
        case Some(l) => actorOf(l) :: actors
      }
    }
  }

  def getPresenterActors(): List[IPresenter[Unit]] = {
    Nil
  }

}