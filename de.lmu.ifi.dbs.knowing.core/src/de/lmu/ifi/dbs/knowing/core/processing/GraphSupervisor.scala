package de.lmu.ifi.dbs.knowing.core.processing

import java.net.URI
import java.util.Properties
import java.util.concurrent.{ TimeUnit, ScheduledFuture, ConcurrentLinkedQueue }
import java.io.PrintWriter
import java.io.IOException
import akka.actor.{ Actor, ActorRef }
import akka.config.Supervision.AllForOneStrategy
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.dispatch._
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.util._
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.model._
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil.{ nodeProperties }
import com.eaio.uuid.UUID
import scala.collection.mutable.{ Map => MutableMap, ListBuffer, SynchronizedQueue }
import scala.collection.JavaConversions._
import System.{ currentTimeMillis => systemTime }
import org.eclipse.sapphire.modeling.xml.{ RootXmlResource, XmlResourceStore }
import org.eclipse.sapphire.modeling.{ ResourceStore, FileResourceStore, Path }
import weka.core.Instances

class GraphSupervisor(dpu: IDataProcessingUnit, uifactory: UIFactory, execPath: URI, directory: IFactoryDirectory) extends Actor with TSender {

  self.faultHandler = AllForOneStrategy(List(classOf[Throwable]), 5, 5000)

  val actors = MutableMap[String, (ActorRef, NodeType)]()
  var actorsByUuid = MutableMap[UUID, String]()

  //Status map holding: UUID -> Reference, Status, Timestamp
  private val statusMap: MutableMap[UUID, (ActorRef, Status, Long)] = MutableMap()
  private val events = ListBuffer[String]()

  var processHistory: IProcessHistory = _

  //Initialize processHistory
  val configuration = dpu.getConfiguration
  configuration.getHistory.getContent.booleanValue match {
    case true =>
      val path = configuration.getOutput.getContent
      val resolved = configuration.getAbsolute.getContent.booleanValue match {
        case true => path
        case false => new Path(execPath.getPath + path.toOSString)
      }
      try {
        //Creating FileResourceStore
        val file = path.toFile
        val store = new RootXmlResource(new XmlResourceStore(new FileResourceStore(file)))
        processHistory = IProcessHistory.TYPE.instantiate(store).asInstanceOf[IProcessHistory]
        processHistory.setName(dpu.getName.getContent)
        self.dispatcher = new LoggableDispatcher("LoggableDispatcher", this)
      } catch {
        case e: IOException => e.printStackTrace()
      }
    case false =>
  }

  //Time-to-life in ms
  private val ttl = 2500L

  def receive = {
    case Register(actor, in, out) => register(actor, in, out)
    case Start | Start() => evaluate
    case UpdateUI | UpdateUI() => uifactory update (self.sender.getOrElse(null), UpdateUI())
    case status: Status => handleStatus(status)
    case uiEvent: UIEvent => handleUIEvent(uiEvent)
    case event: Event => debug(this, "Unhandled Event: " + event)
    case msg => debug(this, "Unhandled Message: " + msg)
  }

  /**
   * Run the process!
   */
  def evaluate {
    initialize
    connectActors
    actorsByUuid = actors map { case (id, (actor, _)) => (actor.getUuid -> id) }
    actors foreach { case (_, (actor, _)) => actor ! Start() }
  }

  /**
   * Configure UIFactory
   * Create and configure actors for each node
   */
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
          actors += (node.getId.getContent -> (actor, node.getType.getContent))
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
    properties setProperty (TLoader.EXE_PATH, execPath.toString)
    new ImmutableProperties(properties)
  }

  /**
   *
   */
  private def connectActors {
    dpu.getEdges foreach (edge => {
      val source = actors(edge.getSource.getContent)._1
      val sourcePort = Some(edge.getSourcePort.getContent)
      val target = actors(edge.getTarget.getContent)._1
      val targetPort = Some(edge.getTargetPort.getContent)
      source ! Register(target, sourcePort, targetPort)
    })
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
        actors foreach { case (_, (actor, _)) => actor stop }
        uifactory update (self, Shutdown())
        processHistory match {
          case null =>
          case history => history.resource.save
        }
        self stop
      }
      case _ => //nothing happens
    }

  }

  /**
   * Sends the UIEvent to every presenter.
   */
  private def handleUIEvent(event: UIEvent) {
    //TODO GraphSupervisor -> UIEvent should contain field "id" to specify presenter
    actors filter {
      case (_, (_, NodeType.PRESENTER)) => true
      case _ => false
    } foreach {
      case (_, (actor, _)) => actor ! event
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

}

class LoggableDispatcher(name: String, supervisor: GraphSupervisor) extends ExecutorBasedEventDrivenDispatcher(name) {

  private val G = classOf[GraphSupervisor]

  private var logEvents = supervisor.configuration.getEventConstraints.map(c => (c.getType.getContent -> c.getLog.getContent.booleanValue)).toMap
  logEvents = GraphSupervisor.logEvents map { case (e, p) => (e -> logEvents.getOrElse(e, p)) }

  private var logNodes = supervisor.configuration.getNodeConstraints.map(c => (c.getNode.getContent -> c.getLog.getContent.booleanValue)).toMap

  private val messages = supervisor.processHistory.getMessages

  override def createMailbox(actor: ActorRef) =
    //This code is copied from base class with little modifications
    mailboxType match {
      case b: UnboundedMailbox =>
        new ConcurrentLinkedQueue[MessageInvocation] with MessageQueue with ExecutableMailbox {
          @inline
          final def dispatcher = LoggableDispatcher.this
          @inline
          final def enqueue(m: MessageInvocation) = {
            //Handle logging to processHistory here
            logHistory(m)
            this.add(m)
          }
          @inline
          final def dequeue(): MessageInvocation = this.poll()
        }
      //BoundedMailbox doesn't log.
      //TODO GraphSupervisor.LoggableDispatcher -> BoundedMailbox logging
      case b: BoundedMailbox =>
        new DefaultBoundedMessageQueue(b.capacity, b.pushTimeOut) with ExecutableMailbox {
          @inline
          final def dispatcher = LoggableDispatcher.this
        }
    }

  /**
   * This method sorts out every actor which should not be
   * logged to processHistory.
   */
  private def logHistory(m: MessageInvocation) {
    (m.sender, m.receiver, m.message) match {
      case (Some(s), r, e: Event) => (s.getActorClass, r.getActorClass) match {
        case (G, _) | (_, G) => //Ignore messages to GraphSupervisor
        case _ =>
          val src = supervisor.actorsByUuid.getOrElse(s.getUuid, "[Internal]" + "[" + s.getActorClass.getSimpleName + "]")
          val trg = supervisor.actorsByUuid.getOrElse(r.getUuid, "[Internal]" + "[" + r.getActorClass.getSimpleName + "]")

          debug(this, "logEvents: " + logEvents + " Empty? " + logEvents.isEmpty)
          //logNodes empty == log all nodes
          if (logNodes.isEmpty) logEvent(src, trg, e)
          //Lookup logNodes => source/target exists == log this msg
          else (logNodes.get(src), logNodes.get(trg)) match {
            case (None, None) => //do not log
            case _ => logEvent(src, trg, e)
          }

      }
      
      case (None, r, e: Event) => r.getActorClass match {
        case G => //Ignore messages to GraphSupervisor
        case _ =>
          val src = "None"
          val trg = supervisor.actorsByUuid.getOrElse(r.getUuid, "[Internal]") + "[" + r.getActorClass.getSimpleName + "]"
          
          debug(this, "logEvents: " + logEvents + " Empty? " + logEvents.isEmpty)
          //logNodes empty == log all nodes
          if (logNodes.isEmpty) logEvent(src, trg, e)
          //Lookup logNodes => source/target exists == log this msg
          else (logNodes.get(trg)) match {
            case None => //do not log
            case _ => logEvent(src, trg, e)
          }
      }
    }
  }

  /**
   * Creates the content which should be logged.
   */
  private def logEvent(src: String, trg: String, msg: Event) = msg match {
    case Results(content, port) => log(src + ":" + port.getOrElse(""), trg, content, EventType.RESULTS)
    case QueryResults(content, _) => log(src, trg, content, EventType.QUERYRESULTS)
    case QueriesResults(c) => log(src, trg, c(0)._2, EventType.QUERIESRESULTS)
    case Query(q) => log(src, trg, q.dataset, EventType.QUERY)
    case Queries(content, _) => log(src, trg, content, EventType.QUERIES)
    case msg: Status => log(src, trg, ResultsUtil.emptyResult, EventType.STATUS)
    case msg: UIEvent => log(src, trg, ResultsUtil.emptyResult, EventType.UIEVENT)
    case _ => log(src, trg, ResultsUtil.emptyResult, EventType.EVENT)
  }

  /**
   * Logs only the messages which should be logged, based on the eventType
   */
  private def log(src: String, trg: String, content: Instances, eventType: EventType) = logEvents(eventType) match {
    case true =>
      val msg = messages.addNewElement
      msg.setType(eventType)
      src.split(":").toList match {
        case Nil =>
        case List(src) => msg.setSource(src)
        case List(src, port) => msg.setSource(src); msg.setSourcePort(port)
        case _ => msg.setSource(src)
      }
      msg.setTarget(trg)
      msg.setContent(content)
    case false => debug(this, "Do not log this " + eventType) //Do not log 
  }
}

object GraphSupervisor {
  val logEvents = EventType.values map (e => (e, false)) toMap
}