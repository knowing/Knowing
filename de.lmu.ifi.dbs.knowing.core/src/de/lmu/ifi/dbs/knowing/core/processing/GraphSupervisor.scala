package de.lmu.ifi.dbs.knowing.core.processing

import java.net.URI
import java.lang.System.{ currentTimeMillis => systemTime }
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.Properties
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{ Map => MutableMap, SynchronizedQueue }
import com.eaio.uuid.UUID
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Scheduler
import akka.config.Supervision.AllForOneStrategy
import akka.dispatch.BoundedMailbox
import akka.dispatch.DefaultBoundedMessageQueue
import akka.dispatch.ExecutableMailbox
import akka.dispatch.ExecutorBasedEventDrivenDispatcher
import akka.dispatch.MessageInvocation
import akka.dispatch.MessageQueue
import akka.dispatch.UnboundedMailbox
import akka.event.EventHandler.debug
import akka.event.EventHandler.error
import akka.event.EventHandler.info
import akka.event.EventHandler.warning
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.model.NodeType
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil.nodeProperties
import de.lmu.ifi.dbs.knowing.core.util._
import java.util.concurrent.ConcurrentLinkedQueue
import java.io.PrintWriter

class GraphSupervisor(dpu: IDataProcessingUnit, uifactory: UIFactory, dpuURI: URI, directory: IFactoryDirectory) extends Actor with TSender {

  self.faultHandler = AllForOneStrategy(List(classOf[Throwable]), 5, 5000)

  private val actors: MutableMap[String, ActorRef] = MutableMap()
  //Status map holding: UUID -> Reference, Status, Timestamp
  private val statusMap: MutableMap[UUID, (ActorRef, Status, Long)] = MutableMap()
  private val events = ListBuffer[String]()
  private var schedules: List[ScheduledFuture[AnyRef]] = List()

  lazy val processHistory = new SynchronizedQueue[MessageInvocation]

  val configuration = dpu.getConfiguration
  configuration.getHistory.getContent match {
    case java.lang.Boolean.TRUE => self.dispatcher = new LoggableDispatcher("LoggableDispatcher", this)
    case java.lang.Boolean.FALSE =>
  }

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
          actor.setDispatcher(self.dispatcher)
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
        case None => error(this, "No factory found for: " + node.getFactoryId.getText)
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
        printHistory
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
  private def finished: Boolean = !statusMap.exists {
    case (_, (_, status, lastTimestamp)) =>
      status match {
        case Running() | Progress(_, _, _) | Waiting() => true
        case _ => false
      }
  }

  private def printHistory = {
    val writer = configuration.getOutput.getContent match {
      case null => new PrintWriter(System.out)
      case path => new PrintWriter(path.toFile)
    }
    processHistory.foldLeft(writer) { (writer, m) =>
      val sender = m.sender match {
        case None => "None"
        case Some(s) => s.getActorClass.getSimpleName + "[" +s.getUuid + "]"
      }
      writer.print(sender)
      writer.print(" -> [")
      writer.print(m.message)
      writer.print("] -> ")
      writer.print(m.receiver.getActorClass.getSimpleName)
      writer.println("[" + m.receiver.getUuid + "]")
      writer
    }
  }.flush

}

class LoggableDispatcher(name: String, supervisor: GraphSupervisor) extends ExecutorBasedEventDrivenDispatcher(name) {

  override def createMailbox(actor: ActorRef) =
    //This code is copied from base class with little modifications
    mailboxType match {
      case b: UnboundedMailbox ⇒
        new ConcurrentLinkedQueue[MessageInvocation] with MessageQueue with ExecutableMailbox {
          @inline
          final def dispatcher = LoggableDispatcher.this
          @inline
          final def enqueue(m: MessageInvocation) = {
            val G = classOf[GraphSupervisor]
            (m.sender, m.receiver) match {
              case (Some(s), r) => (s.getActorClass,r.getActorClass) match {
                case (G,_) | (_,G) => //Ignore messages to GraphSupervisor
                case _ => supervisor.processHistory += m
              }
              case (None, r ) => r.getActorClass match {
                case G => //Ignore messages to GraphSupervisor
                case _ => supervisor.processHistory += m
              }
            }
            this.add(m)
          }
          @inline
          final def dequeue(): MessageInvocation = this.poll()
        }
      case b: BoundedMailbox ⇒
        new DefaultBoundedMessageQueue(b.capacity, b.pushTimeOut) with ExecutableMailbox {
          @inline
          final def dispatcher = LoggableDispatcher.this
        }
    }
}