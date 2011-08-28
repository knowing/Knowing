package de.lmu.ifi.dbs.knowing.core.graph

import akka.actor.{ Actor, ActorRef, Scheduler }
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.config.Supervision.AllForOneStrategy
import com.eaio.uuid.UUID
import java.net.URI
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.util._
import de.lmu.ifi.dbs.knowing.core.processing.{ TPresenter, TSender, TLoader }
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import java.util.Properties
import java.util.concurrent.{ TimeUnit, ScheduledFuture }
import scala.collection.mutable.{ Map => MutableMap, LinkedList }
import System.{ currentTimeMillis => systemTime }
import org.osgi.framework.FrameworkUtil

class GraphSupervisor(dpu: DataProcessingUnit, uifactory: UIFactory, dpuURI: URI, directory: IFactoryDirectory) extends Actor with TSender {

  self.faultHandler = AllForOneStrategy(List(classOf[Throwable]), 5, 5000)

  private val actors: MutableMap[String, ActorRef] = MutableMap()
  //Status map holding: UUID -> Reference, Status, Timestamp
  private val statusMap: MutableMap[UUID, (ActorRef, Status, Long)] = MutableMap()
  private val events: LinkedList[String] = LinkedList()
  private var schedules: List[ScheduledFuture[AnyRef]] = List()

  //Time-to-life in ms
  private val ttl = 2500L

  def receive = {
    case Register(actor, in, out) => register(actor, in, out)
    case Start | Start() => evaluate
    case UpdateUI | UpdateUI() => uifactory update (self.sender.getOrElse(null), UpdateUI())
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
    uifactory setSupervisor (self)
    uifactory update (self, Created())
    uifactory update (self, Progress("initialize", 0, dpu.nodes.length))
    dpu.nodes foreach (node => {
      //      val factory = OSGIUtil.getFactoryService()
      val factory = directory.getFactory(node.factoryId)
      factory match {
        case Some(f) =>
          //Create actor
          val actor = f.getInstance
          self startLink actor
          //Register and link the supervisor
          actor ! Register(self, None)
          //Check for presenter and init
          if (node.nodeType.equals(Node.PRESENTER))
            actor ! UIFactoryEvent(uifactory, node)
          //Configure with properties
          actor ! Configure(configureProperties(node.properties))
          //Add to internal map
          actors += (node.id -> actor)
          statusMap += (actor.getUuid -> (actor, Created(), systemTime))
          uifactory update (actor, Created())
          uifactory update (self, Progress("initialize", 1, dpu.nodes.length))
        case None => warning(this, "No factory found for: " + node.factoryId)
      }
    })
    uifactory update (self, Finished())
  }

  /**
   * Adds the DPU_PATH property to the property configuration
   */
  private def configureProperties(properties: Properties): Properties = {
    properties setProperty (TLoader.EXE_PATH, dpuURI.toString)
    new ImmutableProperties(properties)
  }

  /**
   * 
   */
  private def connectActors {
    dpu.edges foreach (edge => {
      val sid = edge.sourceId.split(":")
      val tid = edge.targetId.split(":")
      val source = actors(sid(0))
      val target = actors(tid(0))
      (sid.length, tid.length) match {
        case (1, 1) =>
          source ! Register(target)
          debug(this, source.getActorClassName + " -> " + target.getActorClassName)
        case (2, 1) =>
          source ! Register(target, Some(sid(1)))
          debug(this, source.getActorClassName + " -> " + target.getActorClassName + ":" + sid(1))
        case (1, 2) =>
          source ! Register(target, None, Some(tid(1)))
          debug(this, source.getActorClassName  + ":" + tid(1) + " -> " + target.getActorClassName)
        case (2, 2) =>
          source ! Register(target, Some(sid(1)), Some(tid(1)))
          debug(this, source.getActorClassName  + ":" + tid(1) + " -> " + target.getActorClassName + ":" + sid(1))
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
    uifactory update (self.sender.getOrElse(null), status)
    self.sender match {
      case Some(a) => statusMap update (a.getUuid, (a, status, systemTime))
      case None => warning(this, "Unkown status message: " + status)
    }
    status match {
      case Ready() | Finished() => if (finished) {
        info(this, "Evaluation finished. Stopping schedules and supervisor")
        //schedules foreach (future => future.cancel(true))
        actors foreach { case (_, actor) => actor stop }
        uifactory update (self, Shutdown())
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