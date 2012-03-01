/*																*\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|	**
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,	**
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|	**
** 																**
** Knowing Framework											**
** Apache License - http://www.apache.org/licenses/				**
** LMU Munich - Database Systems Group							**
** http://www.dbs.ifi.lmu.de/									**
\*																*/
package de.lmu.ifi.dbs.knowing.core.processing

import java.net.URI
import java.util.Properties
import java.util.concurrent.{ TimeUnit, ScheduledFuture, ConcurrentLinkedQueue }
import java.io.{ InputStream, OutputStream, IOException, PrintWriter }
import java.nio.file.{ Paths, Files }
import akka.actor.{ Actor, ActorRef, PoisonPill }
import akka.config.Supervision.OneForOneStrategy
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.dispatch._
import de.lmu.ifi.dbs.knowing.core.factory._
import de.lmu.ifi.dbs.knowing.core.util._
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.service._
import de.lmu.ifi.dbs.knowing.core.model._
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil.{ nodeProperties }
import INodeProperties._
import com.eaio.uuid.UUID
import scala.collection.mutable.{ Map => MutableMap, ListBuffer, SynchronizedQueue }
import scala.collection.JavaConversions._
import System.{ currentTimeMillis => systemTime }
import org.eclipse.sapphire.modeling.xml.{ RootXmlResource, XmlResourceStore }
import org.eclipse.sapphire.modeling.{ ResourceStore, FileResourceStore, Path }
import weka.core.Instances

/**
 * <p> SupervisorActor for a data mining process.</p>
 * <p> This class is used to execute a DPU inside a
 * specific WidgetSystem and on a specified executionPath.</p>
 *
 * @author Nepomuk Seiler
 * @version 0.3
 */
class DPUExecutor(dpu: IDataProcessingUnit,
	uifactory: UIFactory[_],
	execPath: URI,
	directory: IFactoryDirectory,
	modelStore: IModelStore,
	resourceStore: IResourceStore,
	loaderInput: MutableMap[String, InputStream] = MutableMap(),
	saverOutput: MutableMap[String, OutputStream] = MutableMap()) extends Actor with TSender {

	self.faultHandler = OneForOneStrategy(List(classOf[Throwable]), 5, 5000)

	/** NodeID -> (actor, type) */
	var actors = MutableMap[String, (ActorRef, NodeType)]()

	/** UUID -> NodeID */
	var actorsByUuid = MutableMap[UUID, String]()

	/** Status map holding: UUID -> Reference, Status, Timestamp */
	val statusMap: MutableMap[UUID, (ActorRef, Status, Long)] = MutableMap()
	val events = ListBuffer[String]()

	/** data mining process log */
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
				processHistory = IProcessHistory.TYPE.instantiate(store)
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

		case e: ExceptionEvent =>
			warning(self.sender.getOrElse(self), e.details + "\n" + e.throwable + "\n" + e.throwable.getStackTraceString)
			handleStatus(e)

		case status: Status => handleStatus(status)

		case uiEvent: UIEvent => handleUIEvent(uiEvent)

		case event: Event => //Do nothing

		case msg => warning(this, "Unkown Message: " + msg)
	}

	override def postStop = shutdownSupervisor

	/**
	 * Run the process!
	 */
	def evaluate {
		initialize()
		connectActors()
		actorsByUuid = actors map { case (id, (actor, _)) => (actor.getUuid -> id) }
		actors foreach { case (_, (actor, _)) => actor ! Start() }
	}

	/*=======================================================*/
	/*============= CREATE AND INITIALIZE NODES =============*/
	/*=======================================================*/

	/**
	 * Configure UIFactory
	 * Create and configure actors for each node
	 */
	def initialize() {
		initializeUIFactory()
		initializeNodes()
		checkInputOutputMaps()
		uifactory update (self, Finished())
	}

	def initializeUIFactory() {
		uifactory setSupervisor (self)
		uifactory update (self, Created())
		uifactory update (self, Progress("initialize", 0, dpu.getNodes.size))
	}

	def initializeNodes() {
		dpu.getNodes foreach (node => {

			val factory = directory.getFactory(node.getFactoryId.getText)
			factory match {
				case Some(f) =>
					//Create actor
					val actor = f.getInstance
					actor.setDispatcher(self.dispatcher)
					self startLink actor
					//Register and link the supervisor
					//          actor ! Register(self, None)
					//Check for special nodes(presenter,loader,saver) and init
					(node.getId.getContent, node.getType.getContent) match {
						case (_, NodeType.PRESENTER) => actor ! UIFactoryEvent(uifactory, node)

						case (id, NodeType.LOADER) if loaderInput.containsKey(id) =>
							actor ! ConfigureInput(ResultsUtil.UNKOWN_SOURCE, loaderInput(id))
							loaderInput.remove(id)
						case (id, NodeType.SAVER) if saverOutput.containsKey(id) =>
							actor ! ConfigureOutput(ResultsUtil.UNKOWN_SOURCE, saverOutput(id))
							saverOutput.remove(id)
						case _ => //no special treatment
					}

					//Configure with properties
					actor ! Configure(configureProperties(node, f))
					//Add to internal map
					actors += (node.getId.getContent -> (actor, node.getType.getContent))
					statusMap += (actor.getUuid -> (actor, Created(), systemTime))
					uifactory update (actor, Created())
					uifactory update (self, Progress("initialize", 1, dpu.getNodes.size))
				case None =>
					self ! ExceptionEvent(new Exception, "No factory found for: " + node.getFactoryId.getText)
					self ! PoisonPill
			}
		})
	}

	def checkInputOutputMaps() {
		(loaderInput.nonEmpty, saverOutput.nonEmpty) match {
			case (true, true) =>
				self ! ExceptionEvent(new Exception,
					"Input and OuputMap refer to nonexisting nodes. \n loaderInput: " + loaderInput.keySet + " \n saverOutput: " + saverOutput.keySet)
				self ! PoisonPill
			case (true, false) =>
				self ! ExceptionEvent(new Exception, "InputMap refer to nonexisting nodes. \n loaderInput: " + loaderInput.keySet
					+ "\n Loader nodes " + DPUUtil.loaderNodes(dpu).foreach(n => print(n + ", ")))
				self ! PoisonPill
			case (false, true) =>
				self ! ExceptionEvent(new Exception, "OutputMap refer to nonexisting nodes. \n saverOutput: " + saverOutput.keySet
					+ "\n Saver nodes " + DPUUtil.saverNodes(dpu).foreach(n => print(n + ", ")))
				self ! PoisonPill
			case (false, false) => debug(this, "All input/output maps have been processed successfully")
		}
	}

	/*=======================================================*/
	/*================== CONFIGURE NODES ====================*/
	/*=======================================================*/

	/**
	 * Adds the DPU_PATH property to the property configuration
	 */
	def configureProperties(node: INode, f: TFactory): Properties = {
		val properties = nodeProperties(node)
		properties setProperty (TLoader.EXE_PATH, execPath.toString)

		val defProperties = new Properties(f.createDefaultProperties)
		defProperties.put(INodeProperties.DEBUG, "false")

		//Check FILE | URL | DIR properties
		val urlKey = properties.containsKey(INodeProperties.URL)
		val dirKey = properties.containsKey(DIR)
		val fileKey = properties.containsKey(FILE)
		(urlKey, fileKey, dirKey) match {
			case (true, _, _) =>
			case (_, true, _) => TStreamResolver.resolveFromFileSystem(properties, FILE, TStreamResolver.acceptAbsoluteFile) match {
				case Some(p) if Files.exists(p) => debug(this, "File found " + p) //perfect!
				case Some(p) if !Files.exists(p) =>
					warning(this, "File input for Node " + node.getId.getContent + " could be resolved, but doesn't exists " + p)
					resourceStore.getResource(node) match {
						case None => warning(this, "Default resource [" + properties.getProperty(FILE) + "] for Node " + node.getId.getContent + " couldn't be found")
						case Some(url) =>
							debug(this, "Set input URL to " + url.toString)
							properties.removeKey(FILE)
							properties.setProperty(INodeProperties.URL, url.toString)
					}
				case None =>
					warning(this, "File input for Node " + node.getId.getContent + " could not be resolved. Wrong filename or path.")
					val file = properties.getProperty(FILE)
					resourceStore.getResource(file) match {
						case None => warning(this, "Default resource [" + file + "] for Node [" + node.getId.getContent + "] couldn't be found")
						case Some(url) =>
							debug(this, "Set input URL to " + url.toString)
							properties.removeKey(FILE)
							properties.setProperty(INodeProperties.URL, url.toString)
					}

			}

			case (_, false, dir) =>
			case (_, _, _) =>
		}
		//Check DESERIALIZE property
		properties.containsKey(DESERIALIZE) match {
			case true =>
				debug(this, "Node " + node.getId.getContent + " has deserialize property")
				TStreamResolver.resolveFromFileSystem(properties, DESERIALIZE, TStreamResolver.acceptAbsoluteFile) match {
					case Some(p) if Files.exists(p) => debug(this, " File exists " + p) //perfect!
					case Some(p) if !Files.exists(p) =>
						warning(this, "Deserialize input for Node " + node.getId.getContent + " could be resolved, but doesn't exists " + p)
						modelStore.getModel(node) match {
							case None => warning(this, "Default model [" + properties.getProperty(DESERIALIZE) + "] for Node [" + node.getId.getContent + "] couldn't be found")
							case Some(url) =>
								debug(this, "Set input DESERIALIZE to " + url.toString)
								properties.setProperty(DESERIALIZE, url.toString)
						}

					case None =>
						warning(this, "Deserialize input for Node " + node.getId.getContent + " could not be resolved. Wrong filename or path.")
						val file = properties.getProperty(DESERIALIZE)
						modelStore.getModel(file) match {
							case None => warning(this, "Default model [" + file + "] for Node [" + node.getId.getContent + "] couldn't be found")
							case Some(url) =>
								debug(this, "Set input DESERIALIZE to " + url.toString)
								properties.setProperty(DESERIALIZE, url.toString)
						}
				}
			case false => debug(this, "Node " + node.getId.getContent + " has NO deserialize property")
		}

		properties foreach { case (v, k) => defProperties setProperty (v, k) }
		new ImmutableProperties(defProperties)
	}

	/*=======================================================*/
	/*=================== CONNECT NODES =====================*/
	/*=======================================================*/

	/**
	 *
	 */
	def connectActors() {
		dpu.getEdges foreach (edge => {
			val source = actors(edge.getSource.getContent)._1
			val sourcePort = Some(edge.getSourcePort.getContent)
			val target = actors(edge.getTarget.getContent)._1
			val targetPort = Some(edge.getTargetPort.getContent)
			source ! Register(target, sourcePort, targetPort)
		})
	}

	/*=======================================================*/
	/*=============== LIFECYCLE MANAGEMENT ==================*/
	/*=======================================================*/

	/**
	 * On each status update the supervisor checks if the
	 * process has finished.
	 */
	def handleStatus(status: Status) {
		uifactory update (self.sender.getOrElse(null), status)
		self.sender match {
			case Some(a) => statusMap update (a.getUuid, (a, status, systemTime))
			case None => warning(this, "Unkown status message: " + status)
		}
		status match {

			//Only check if finished if a "finishing" event arrives
			case Ready() | Finished() => if (finished) {
				info(this, "Evaluation finished. Stopping schedules and supervisor")
				//schedules foreach (future => future.cancel(true))
				shutdownSupervisor
				uifactory update (self, Shutdown())
				self stop
			}

			//Process seems to be going on
			case _ => //nothing happens
		}

	}

	def shutdownSupervisor() {
		processHistory match {
			case null =>
			case history => history.resource.save
		}
		actors foreach { case (_, (actor, _)) => actor stop }

		//Set ActorRefs free
		actors = MutableMap[String, (ActorRef, NodeType)]()
		actorsByUuid = MutableMap[UUID, String]()
	}

	/**
	 * Sends the UIEvent to every presenter.
	 */
	def handleUIEvent(event: UIEvent) {
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
	def finished: Boolean = !statusMap.exists {
		case (_, (_, status, lastTimestamp)) =>
			status match {
				case Running() | Progress(_, _, _) | Waiting() => true
				case _ => false
			}
	}

}

object DPUExecutor {
	val logEvents = EventType.values map (e => (e, false)) toMap
}

/**
 * <p> Dispatcher which uses the mailboxes of each actor to log
 * messages send between actors. This Dispatcher is only used if
 * the "history" property is enabled in the DataProcessingUnit (DPU).</p>
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
class LoggableDispatcher(name: String, supervisor: DPUExecutor) extends ExecutorBasedEventDrivenDispatcher(name) {

	/** Do not log messages send to GraphSupervisor */
	private val G = classOf[DPUExecutor]

	private var logEvents = supervisor.configuration.getEventConstraints.map(c => (c.getType.getContent -> c.getLog.getContent.booleanValue)).toMap
	logEvents = logEvents.isEmpty match {
		case true => DPUExecutor.logEvents map { case (e, p) => (e -> !p) }
		case false => DPUExecutor.logEvents map { case (e, p) => (e -> logEvents.getOrElse(e, p)) }
	}

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
		(m.channel, m.receiver, m.message) match {
			case (s: ActorRef, r, e: Event) => (s.getActorClass, r.getActorClass) match {
				case (G, _) | (_, G) => //Ignore messages to GraphSupervisor
				case _ =>
					val src = supervisor.actorsByUuid.getOrElse(s.getUuid, "[Internal]" + "[" + s.getActorClass.getSimpleName + "]")
					val trg = supervisor.actorsByUuid.getOrElse(r.getUuid, "[Internal]" + "[" + r.getActorClass.getSimpleName + "]")

					//logNodes empty == log all nodes
					if (logNodes.isEmpty) logEvent(src, trg, e)
					//Lookup logNodes => source/target exists == log this msg
					else (logNodes.get(src), logNodes.get(trg)) match {
						case (None, None) => //do not log
						case _ => logEvent(src, trg, e)
					}

			}
			case (_, r, e: Event) => r.getActorClass match {
				case G => //Ignore messages to GraphSupervisor
				case _ =>
					val src = "None"
					val trg = supervisor.actorsByUuid.getOrElse(r.getUuid, "[Internal]") + "[" + r.getActorClass.getSimpleName + "]"

					//logNodes empty == log all nodes
					if (logNodes.isEmpty) logEvent(src, trg, e)
					//Lookup logNodes => source/target exists == log this msg
					else (logNodes.get(trg)) match {
						case None => //do not log
						case _ => logEvent(src, trg, e)
					}
			}
			case (_, r, m) =>
		}
	}

	/**
	 * Creates the content which should be logged.
	 */
	private def logEvent(src: String, trg: String, msg: Event) = msg match {
		case Results(content, port, q) => log(src + ":" + port.getOrElse(""), trg, content, EventType.RESULTS)
		case Query(q) => log(src, trg, q, EventType.QUERY)
		case msg: Status => log(src, trg, ResultsUtil.emptyResult, EventType.STATUS)
		case msg: UIEvent => log(src, trg, ResultsUtil.emptyResult, EventType.UIEVENT)
		case _ => log(src, trg, ResultsUtil.emptyResult, EventType.EVENT)
	}

	/**
	 * Logs only the messages which should be logged, based on the eventType
	 */
	private def log(src: String, trg: String, content: Instances, eventType: EventType) = logEvents(eventType) match {
		case true => try {
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
		} catch {
			case e: Exception => warning(this, "Logging failed: " + e.getMessage)
		}
		case false => //Do not log 
	}
}

