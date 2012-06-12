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
package de.lmu.ifi.dbs.knowing.core.service

import java.net.URI
import java.util.Properties
import akka.actor.ActorRef
import java.io.{ InputStream, OutputStream }
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.exceptions.ValidationException
import de.lmu.ifi.dbs.knowing.core.exceptions.KnowingException
import scala.collection.mutable.{ Map => MutableMap }
import akka.actor.ActorSystem
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

/**
 * OSGi service responsible for starting a data mining process.
 *
 * @author Nepomuk Seiler
 * @version 0.2
 */
trait IEvaluateService {

	/**
	 * Executes the given dpu.
	 * Choose UIFactory by ID
	 *
	 * @param dpu - the DataProcessingUnit - not null
	 * @param execPath - execution path - not null
	 * @param uiFactory - choose uiSystem and where to present - not null
	 * @param system - system to run in - can be null (system of UIFactory is chosen)
	 * @param parameters - customize the dpu - can be null (no parameters)
	 * @param input - input streams - can be null
	 * @param output - output streams - can be null
	 *
	 * @return ActorRef to the execution actor
	 */
	@throws(classOf[ValidationException])
	@throws(classOf[KnowingException])
	def evaluate(dpu: IDataProcessingUnit, execPath: URI,
		uiFactory: UIFactory[_],
		system: ActorSystem,
		parameters: Properties,
		input: MutableMap[String, InputStream],
		output: MutableMap[String, OutputStream]): ActorRef

	/**
	 * Resolve all configure from the config file.
	 *
	 * @param config - configuration to run
	 * @see EvaluationProperties
	 */
	@throws(classOf[ValidationException])
	@throws(classOf[KnowingException])
	def evaluate(config: Config): ActorRef

}

/**
 * Sample config
 *
 * <code>
 * dpu = my-dpu-id
 * execution-path = "file:///home/user/executionpath"
 * uifactory = "de.lmu.ifi.dbs.knowing.core.swt.presenterView"
 * parameters.p1 = true
 * parameters.p2 = "/home/user/"
 * parameters.p3 = "de.mukis.typesafe.config.ConfigApp"
 * parameters.p4 = 2.3
 * parameters.p5 = 75
 * </code>
 *
 */
object EvaluationProperties {

	/** This or DPU_PATH property must be present */
	val DPU = "dpu"

	/** This or DPU property must be present */
	val DPU_PATH = "dpu-path"

	/** Must be present */
	val EXECUTION_PATH = "execution-path"

	/** Must be present */
	val UIFACTORY = "uifactory"

	/** Optional. If you decide to run on another system than the UIFactory system */
	val SYSTEM = "system"

	def buildConfig(dpuId: String, execPath: URI, uiFactoryId: String, systemId: String): Config = {
		val properties = new Properties
		properties.setProperty(DPU, dpuId);
		properties.setProperty(EXECUTION_PATH, execPath.toString)
		properties.setProperty(UIFACTORY, uiFactoryId)
		properties.setProperty(SYSTEM, systemId)
		ConfigFactory.parseProperties(properties)
	}
}