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
package de.lmu.ifi.dbs.knowing.launcher

import de.lmu.ifi.dbs.knowing.core.util.DPUUtil._

import com.typesafe.config._
import java.util.{ Map => JMap }
import java.net.URI
import scala.collection.JavaConversions._

class LaunchConfiguration(config: Config) {

	// validate vs. reference.conf
	//config.checkValid(ConfigFactory.defaultReference())

	private val dpuUriString = config.getString("dpu.uri")
	private val dpuUrl = new URI(dpuUriString).toURL

	val dpu = deserialize(dpuUrl)

	val executionPath = config.getString("dpu.executionpath")

	val parameters = config.getConfig("dpu.parameters").entrySet
		.map(e => (e.getKey -> e.getValue.render)).toMap
}

object LaunchConfiguration {

	val APPLICATION_CONF = "knowing.launcher.configuration"

	val REQUIRED_BUNDLES = List(
		"ch.qos.logback.classic@default:default",
		"ch.qos.logback.core@default:default",
		"com.typesafe.config@default:default",
		"de.lmu.ifi.dbs.knowing.core.logging@default:false",
		"de.lmu.ifi.dbs.knowing.core@default:true",
		"de.lmu.ifi.dbs.knowing.debug.presenter@default:true",
		"de.lmu.ifi.dbs.knowing.launcher@default:true",
		"de.lmu.ifi.dbs.knowing.presenter@default:default",
		"nz.ac.waikato.cs.weka@default:default",
		"org.codehaus.aspectwerkz@default:default",
		"org.eclipse.core.contenttype@default:default",
		"org.eclipse.core.expressions@default:default",
		"org.eclipse.core.filesystem@default:default",
		"org.eclipse.core.jobs@default:default",
		"org.eclipse.core.resources@default:default",
		"org.eclipse.core.runtime@default:true",
		"org.eclipse.core.variables@default:default",
		"org.eclipse.equinox.app@default:default",
		"org.eclipse.equinox.common@2:true",
		"org.eclipse.equinox.ds@1:true",
		"org.eclipse.equinox.preferences@default:default",
		"org.eclipse.equinox.registry@default:default",
		"org.eclipse.equinox.util@default:default",
		"org.eclipse.equinox.weaving.hook@default:false",
		"org.eclipse.osgi.services@default:default",
		"org.eclipse.osgi@-1:true",
		"org.eclipse.sapphire.java@default:default",
		"org.eclipse.sapphire.modeling.xml@default:default",
		"org.eclipse.sapphire.modeling@default:default",
		"org.eclipse.sapphire.osgi.fragment@default:false",
		"org.eclipse.sapphire.osgi@default:default",
		"org.eclipse.sapphire.platform@default:default",
		"org.eclipse.sapphire.workspace@default:default",
		"org.eclipse.wst.common.uriresolver@default:default",
		"org.scala-ide.scala.library@default:default",
		"slf4j.api@default:default")
		
}