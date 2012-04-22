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
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-04-19
 */
class Activator extends BundleActivator {

	def start(context: BundleContext) = {
		val configUriString = System.getProperty(LaunchConfiguration.APPLICATION_CONF)

		if (configUriString != null && configUriString.nonEmpty) {
			val configUrl = new URI(configUriString).toURL
			val config = ConfigFactory.parseURL(configUrl)
			println(config.root.render)

			val launchConfig = new LaunchConfiguration(config)
			val dpu = launchConfig.dpu
			val reference = Option(context.getServiceReference(classOf[IEvaluateService]))
			if (reference.isDefined) {
				try {
					val evaluateService = context.getService(reference.get)
					evaluateService.evaluate(dpu, Paths.get(System.getProperty("user.home")).toUri)
				} catch {
					case e: ValidationException => System.err.println(e.getErrors());
				}

			}

		}

	}

	def stop(context: BundleContext) = {}

}
