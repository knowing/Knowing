package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.{ Actor, ActorRef, Scheduler }
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.config.Supervision.AllForOneStrategy
import com.eaio.uuid.UUID
import java.net.URI
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.util._
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil.{ nodeProperties }
import java.util.Properties
import java.util.concurrent.{ TimeUnit, ScheduledFuture }
import scala.collection.mutable.{ Map => MutableMap, LinkedList }
import scala.collection.JavaConversions._
import System.{ currentTimeMillis => systemTime }
import org.osgi.framework.FrameworkUtil
import de.lmu.ifi.dbs.knowing.core.model.NodeType

class GraphSupervisor(dpu: IDataProcessingUnit, uifactory: UIFactory, dpuURI: URI, directory: IFactoryDirectory) extends Actor with TSender {

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
    case msg => debug(this, "Unhandled Message: " + msg)
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
    uifactory update (self, Progress("initialize", 0, dpu.getNodes.size))
    dpu.getNodes foreach (node => {
      //      val factory = OSGIUtil.getFactoryService()
      val factory = directory.getFactory(node.getFactoryId.getText)
      factory match {
        case Some(f) =>
          //Create actor
          val actor = f.getInstance
          self startLink actor
          //Register and link the supervisor
          actor ! Register(self, None)
          //Check for presenter and init
          if (node.getType.getContent.equals(NodeType.PRESENTER))
            actor ! UIFactoryEvent(uifactory, node)
          //Configure with properties
          actor ! Configure(configureProperties(nodeProperties(node)))
          //Add to internal map
          actors += (node.getId.getContent -> actor)
          statusMap += (actor.getUuid -> (actor, Created(), systemTime))
          uifactory update (actor, Created())
          uifactory update (self, Progress("initialize", 1, dpu.getNodes.size))
        case None => warning(this, "No factory found for: " + node.getFactoryId.getText)
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
    dpu.getEdges foreach (edge => {
      val source = actors(edge.getSource.getContent)
      val sourcePort = Some(edge.getSourcePort.getContent)
      val target = actors(edge.getTarget.getContent)
      val targetPort = Some(edge.getTargetPort.getContent)
      source ! Register(target, sourcePort, targetPort)
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