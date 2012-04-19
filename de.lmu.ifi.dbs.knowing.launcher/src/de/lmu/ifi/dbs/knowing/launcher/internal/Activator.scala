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
		
		val defRef = ConfigFactory.defaultReference(getClass.getClassLoader)
		val parms = defRef.getConfig("dpu.parameters")
		println(parms)

	}

	def stop(context: BundleContext) = {}

}
