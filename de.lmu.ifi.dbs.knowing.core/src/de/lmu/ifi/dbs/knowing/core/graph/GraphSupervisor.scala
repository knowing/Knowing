package de.lmu.ifi.dbs.knowing.core.graph

import akka.actor.{ Actor, ActorRef, Scheduler }
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.config.Supervision.AllForOneStrategy
import com.eaio.uuid.UUID
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.util._
import de.lmu.ifi.dbs.knowing.core.processing.{ TPresenter, TSender, TLoader }
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.events._
import java.util.Properties
import java.util.concurrent.{ TimeUnit, ScheduledFuture }
import scala.collection.mutable.{ Map => MutableMap, LinkedList }
import System.{ currentTimeMillis => systemTime }
import org.osgi.framework.FrameworkUtil

class GraphSupervisor(dpu: DataProcessingUnit, uifactory: UIFactory, dpuDir: String) extends Actor with TSender {

  self.faultHandler = AllForOneStrategy(List(classOf[Throwable]), 5, 5000)

  private val actors: MutableMap[String, ActorRef] = MutableMap()
  //Status map holding: UUID -> Reference, Status, Timestamp
  private val statusMap: MutableMap[UUID, (ActorRef, Status, Long)] = MutableMap()
  private val events: LinkedList[String] = LinkedList()
  private var schedules: List[ScheduledFuture[AnyRef]] = List()

  //Time-to-life in ms
  private val ttl = 2500L

  def receive = {
    case Register(actor, port) => addListener(actor, port)
    case Start | Start() => evaluate
    case UpdateUI | UpdateUI() => uifactory update
    case status: Status => handleStatus(status)
    case event: Event => events + event.getClass().getSimpleName
    case msg => debug(this, "Unkown Message: " + msg)
  }

  def evaluate {
    initialize
    connectActors
    actors foreach { case (_, actor) => actor ! Start }
    //schedule
  }

  private def initialize {
    dpu.nodes foreach (node => {
      val factory = Util.getFactoryService(node.factoryId)
      factory match {
        case Some(f) =>
          //Create actor
          val actor = f.getInstance
          self startLink actor
          //Register and link the supervisor
          actor ! Register(self, None)
          //Check for presenter and init
          if (node.nodeType.equals(Node.PRESENTER))
            actor !! UIFactoryEvent(uifactory, node)
          //Configure with properties
          actor !! Configure(configureProperties(node.properties))
          //Add to internal map
          actors += (node.id -> actor)
          statusMap += (actor.getUuid -> (actor, Created(), systemTime))
        case None => warning(this, "No factory found for: " + node.factoryId)
      }
    })
  }

  /**
   * Adds the DPU_PATH property to the property configuration
   */
  private def configureProperties(properties: Properties): Properties = {
    properties setProperty (TLoader.DPU_PATH, dpuDir)
    properties
  }

  /**
   *
   */
  private def connectActors {
    dpu.edges foreach (edge => {
      val sid = edge.sourceId.split(":")
      val source = actors(sid(0))
      val target = actors(edge.targetId)
      val length = sid.length
      length match {
        case 1 =>
          source !! Register(target, None)
          debug(this, source.getActorClassName + " -> " + target.getActorClassName)
        case 2 =>
          source !! Register(target, Some(sid(1)))
          debug(this, source.getActorClassName + " -> " + target.getActorClassName + ":" + sid(1))
      }
    })
  }

  /**
   * <p>Checks actors if they are alive.</p>
   */
  private def schedule {
    actors foreach {
      case (_, actor) => schedules = Scheduler.schedule(actor, Alive, 500, 2500, TimeUnit.MILLISECONDS) :: schedules
    }
  }

  private def handleStatus(status: Status) {
    self.sender match {
      case Some(a) => statusMap update (a.getUuid, (a, status, systemTime))
      case None => warning(this, "Unkown status message: " + status)
    }
    status match {
      case Ready() | Finished() => if (finished) {
        info(this, "Evaluation finished. Stopping schedules and supervisor")
        //schedules foreach (future => future.cancel(true))
        actors foreach { case (_, actor) => actor stop }
        self stop
      }
      case _ => //nothing happens
    }

  }

  /**
   * <p>Evaluation process is finished if: <p>
   * 1) All actors have status Finished | Ready
   * 2) One actor timed out
   */
  private def finished: Boolean = {
    //    val timestamp = systemTime
    //    if (ttl - 500 > Math.abs(timestamp - this.timestamp))
    //      return false
    //    this.timestamp = timestamp

    !statusMap.exists {
      case (_, (_, status, lastTimestamp)) =>
        status match {
          case Running() | Progress(_, _, _) | Waiting() => true
          case _ => false
        }
      //TODO GraphSupervisor -> Actor Timeout error handling
      //        ttl < Math.abs(timestamp - lastTimestamp)
    }
  }

}