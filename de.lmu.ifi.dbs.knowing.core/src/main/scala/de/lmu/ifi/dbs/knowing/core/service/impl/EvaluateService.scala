/*                                                              *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
**                                                              **
** Knowing Framework                                            **
** Apache License - http://www.apache.org/licenses/             **
** LMU Munich - Database Systems Group                          **
** http://www.dbs.ifi.lmu.de/                                   **
\*                                                              */
package de.lmu.ifi.dbs.knowing.core.service.impl

import java.net.URI
import java.io.{ InputStream, OutputStream }
import akka.actor.{ ActorSystem, ActorRef, Props }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.exceptions._
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.processing.DPUExecutor
import de.lmu.ifi.dbs.knowing.core.service._
import de.lmu.ifi.dbs.knowing.core.service.EvaluationProperties._
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.util.{ DPUValidation, DPUUtil }
import scala.collection.mutable.{ Map => MutableMap, HashMap }
import scala.collection.mutable.ArrayBuffer
import org.slf4j.LoggerFactory
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import java.net.MalformedURLException
import java.net.URL

/**
 * Default implementation for the EvaluationService
 *
 * @author Nepomuk Seiler
 * @version 0.2
 */
class EvaluateService extends IEvaluateService {

	private lazy val log = LoggerFactory.getLogger(classOf[IEvaluateService])

	/** 1..1 relation */
	private var factoryDirectory: IFactoryDirectory = _

	/** 1..1 relation */
	private var dpuDirectory: IDPUDirectory = _

	/** 1..1 relation */
	private var modelStore: IModelStore = _

	/** 1..1 relation */
	private var resourceStore: IResourceStore = _

	/** 0..n relation */
	private var uiFactories = new ArrayBuffer[UIFactory[_]]()

	/** 1..1 relation */
	private var actorSystemManager: IActorSystemManager = _

	@throws(classOf[ValidationException])
	@throws(classOf[KnowingException])
	def evaluate(config: Config): ActorRef = {

		//Resolve DPU
		val dpu = (config.hasPath(DPU), config.hasPath(DPU_PATH)) match {
			case (true, _) => dpuDirectory.getDPU(config.getString(DPU)) getOrElse {
				throw new ConfigException.BadValue("dpu", "DPU with id " + config.getString(DPU) + " could not be found")
			}
			case (false, true) => try {
				val url = new URL(config.getString(DPU_PATH))
				DPUUtil.deserialize(url)
			} catch {
				case e: MalformedURLException => throw new ConfigException.BadValue(DPU, "DPU path incorrect", e)
			}
			case _ => throw new ConfigException.Missing(DPU + " or " + DPU_PATH)
		}
		
		//Resolve execution path
		val execPath = try {
			if(!config.hasPath(EXECUTION_PATH))
				throw new ConfigException.Missing(EXECUTION_PATH)
			val url = new URL(config.getString(EXECUTION_PATH))
			url.toURI
		} catch {
			case e: MalformedURLException => throw new ConfigException.BadValue(DPU, "Execution path incorrect", e)
		}
		
		//Resolve UIFactory
		if(!config.hasPath(UIFACTORY))
			throw new ConfigException.Missing(UIFACTORY)
		
		val uiFactoryId = config.getString(UIFACTORY)
		val uiFactory =	uiFactories.find(e => e.getId.equals(uiFactoryId)) getOrElse {
			throw new ConfigException.BadValue(UIFACTORY, "No UIFactory with id " + uiFactoryId + " found")
		}
		
		//Resolve ActorSystem
		val system = config.hasPath(SYSTEM) match {
			case false => uiFactory.getSystem
			case true => actorSystemManager.getSystem(config.getString(SYSTEM)) getOrElse {
				throw new ConfigException.BadValue(UIFACTORY, "No ActorSystem with name " + config.getString(SYSTEM) + " found")
			}
		}
		
		//Evaluate
		evaluate(dpu, execPath, uiFactory, system, null, null)
	}

	/**
	 * Instantiates DPUExecturo and runs the DPU
	 * @see IEvaluationService
	 */
	@throws(classOf[ValidationException])
	@throws(classOf[KnowingException])
	def evaluate(dpu: IDataProcessingUnit, execPath: URI,
		ui: UIFactory[_],
		system: ActorSystem,
		input: MutableMap[String, InputStream],
		output: MutableMap[String, OutputStream]): ActorRef = {

		DPUValidation.runtime(dpu) match {
			case validation if validation.hasErrors() => throw new ValidationException("Error on validation.", validation)
			case validation if validation.hasWarnings() => log.warn("DPU has warnings: " + validation.getWarnings)
			case validation => log.info("DPU validation successfull!")
		}

		val io = (input, output) match {
			case (null, null) => (new HashMap[String, InputStream], new HashMap[String, OutputStream])
			case (input, null) => (input, new HashMap[String, OutputStream])
			case (null, output) => (new HashMap[String, InputStream], output)
			case (input, output) => (input, output)
		}

		val executor = system match {
			case null => ui.getSystem.actorOf(Props(new DPUExecutor(dpu, ui, execPath, factoryDirectory, modelStore, resourceStore, io._1, io._2)))
			case _ => system.actorOf(Props(new DPUExecutor(dpu, ui, execPath, factoryDirectory, modelStore, resourceStore, io._1, io._2)))
		}

		executor ! Start()
		executor
	}

	def activate() {
		log.debug("EvaluateService activated")
	}

	/** bind factory service */
	def bindDirectoryService(service: IFactoryDirectory) = factoryDirectory = service

	/** unbind factory service */
	def unbindDirectoryService(service: IFactoryDirectory) = factoryDirectory = null

	/** bind dpu service */
	def bindDPUDirectoryService(service: IDPUDirectory) = dpuDirectory = service

	/** unbind dpu service */
	def unbindDPUDirectoryService(service: IDPUDirectory) = dpuDirectory = null

	/** bind factory service */
	def bindModelStoreService(service: IModelStore) = modelStore = service

	/** unbind factory service */
	def unbindModelStoreService(service: IModelStore) = modelStore = null

	/** bind factory service */
	def bindResourceStoreService(service: IResourceStore) = resourceStore = service

	/** unbind factory service */
	def unbindResourceStoreService(service: IResourceStore) = resourceStore = null

	/** bind UIFactory service */
	def bindUIFactory(service: UIFactory[_]) = uiFactories += service

	/** unbind UIFactory service */
	def unbindUIFactory(service: UIFactory[_]) = uiFactories -= service

	/** bind ActorSystemManager */
	def bindActorSystemManager(service: IActorSystemManager) = actorSystemManager = service

	/** unbind ActorSystemManager */
	def unbindActorSystemManager(service: IActorSystemManager) = actorSystemManager = null
}