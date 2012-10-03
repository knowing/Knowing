/*                                                               *\
 ** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
 ** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
 ** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
 **                                                              **
 ** Knowing Framework                                            **
 ** Apache License - http://www.apache.org/licenses/             **
 ** LMU Munich - Database Systems Group                          **
 ** http://www.dbs.ifi.lmu.de/                                   **
\*                                                               */
package de.lmu.ifi.dbs.knowing.launcher.internal

import org.osgi.framework.{ BundleContext, BundleActivator }
import de.lmu.ifi.dbs.knowing.launcher.LaunchConfiguration
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil
import de.lmu.ifi.dbs.knowing.core.service.IEvaluateService
import de.lmu.ifi.dbs.knowing.core.exceptions.ValidationException
import com.typesafe.config.ConfigFactory
import java.net.URI
import java.nio.file.Paths

/**
 * To launch an DPU with the launcher:
 * 
 * LaunchConfiguration.APPLICATION_CONF_FILE (config.file) => path to application.conf 
 * 
 * Command could look like: 
 * <code>launch /home/user/knowing/naiveBayes/application.conf</code>
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-04-19
 */
class Activator extends BundleActivator {

	/**
	 * The evaluation starts, when the bundle is activated.
	 */
	def start(context: BundleContext) = {
		val configUriString = System.getProperty(LaunchConfiguration.APPLICATION_CONF_FILE)
		
		//TODO check for debug.launcher if present and don't launch
		/*
		if (configUriString != null && configUriString.nonEmpty) {
			val configUrl = new URI(configUriString).toURL
			val config = ConfigFactory.parseURL(configUrl)

			val launchConfig = new LaunchConfiguration(config)
			val dpu = launchConfig.dpu
			val reference = Option(context.getServiceReference(classOf[IEvaluateService]))
			if (reference.isDefined) {
				try {
					val evaluateService = context.getService(reference.get)
					//TODO Evaluate
				} catch {
					case e: ValidationException => System.err.println(e.getErrors());
				}
			}
		}
		*/
	}

	def stop(context: BundleContext) = {}

}
