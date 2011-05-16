package de.lmu.ifi.dbs.knowing.core.graph

import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.util._
import de.lmu.ifi.dbs.knowing.core.processing.{ TPresenter, TSender }
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.events._
import akka.actor.{ Actor, ActorRef }
import akka.actor.Actor.actorOf
import org.osgi.framework.FrameworkUtil
import akka.event.EventHandler

class GraphSupervisor(val dpu: DataProcessingUnit, val uifactory: UIFactory) extends Actor with TSender {

  var actors: Map[String, ActorRef] = Map()
  var events:List[String] = Nil

  def receive = {
    case Register(actor) =>
      addListener(actor)
    case Start => evaluate
    case event: Event => events + event.getClass().getSimpleName
    case _ => EventHandler.debug(this,"----") 
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
//          self link (actor)
          actor ! Register(self)
          //Check for presenter and init
          if (node.nodeType.equals(Node.PRESENTER))
            actor !! UIFactoryEvent(uifactory, node)
          //Configure with properties
          actor !! Configure(node.properties)
          //Add to internal map
          actors += (node.id -> actor)
         EventHandler.debug(this,"#Add " + node.properties)
          case None =>EventHandler.warning(this,"No factory found for: " + node.factoryId)
      }
    })
  }

  private def connectActors {
    dpu.edges foreach (edge => {
      val source = actors(edge.sourceId)
      val target = actors(edge.targetId)
      source !! Register(target)
      EventHandler.debug(this,source.getActorClassName + " -> " + target.getActorClassName)
    })
  }

  def getPresenterActors(): List[TPresenter[Unit]] = {
    Nil
  }

}