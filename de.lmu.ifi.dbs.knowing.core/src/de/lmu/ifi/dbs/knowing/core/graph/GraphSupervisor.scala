package de.lmu.ifi.dbs.knowing.core.graph

import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util._
import de.lmu.ifi.dbs.knowing.core.processing.{ TPresenter, TSender }
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.events._
import akka.actor.{ Actor, ActorRef }
import akka.actor.Actor.actorOf

import org.osgi.framework.FrameworkUtil

class GraphSupervisor(val dpu: DataProcessingUnit) extends Actor with TSender {

  var actors: Map[String, ActorRef] = Map()

  def receive = {
    case Register(actor) =>
      addListener(actor)
      log info ("Event occured: Register")
    case Start => evaluate
    case event: Event => log.info("Event occured: " + event.getClass().getSimpleName)
    case _ => log.error("Unkown message")
  }

  def evaluate {
    initialize
    connectActors
    actors foreach { case (_, actor) => actor ! Start }
  }

  private def initialize {
    dpu.nodes foreach (node => {
      val factory = Util.getFactoryService(node.factoryId)
      factory match {
        case Some(f) =>
          //Create actor
          val actor = f.getInstance.start
          //Register and link the supervisor
          self link (actor)
          actor ! Register(self)
          //Configure with properties
          actor ! Configure(node.properties)
          //Add to internal map
          log debug(node.id + " -> " + actor)
          actors += (node.id -> actor)
        case None => println("ERROR")
      }
    })
  }

  private def connectActors {
    dpu.edges foreach (edge => {
      val source = actors(edge.sourceId)
      val target = actors(edge.targetId)
      source !! Register(target)
    })
  }

  def getPresenterActors(): List[TPresenter[Unit]] = {
    Nil
  }

}