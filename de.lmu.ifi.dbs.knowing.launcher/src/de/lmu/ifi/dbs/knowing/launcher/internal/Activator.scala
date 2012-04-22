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

import org.osgi.framework.{BundleContext,BundleActivator}
import de.lmu.ifi.dbs.knowing.launcher.LaunchConfiguration
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil
import com.typesafe.config.ConfigFactory
import java.net.URI

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
		}

	}

	def stop(context: BundleContext) = {}

}
