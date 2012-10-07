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
import java.util.{ Map => JMap, List => JList }
import java.net.URI
import scala.collection.JavaConversions._

class LaunchConfiguration(config: Config) {

  // validate vs. reference.conf
  //config.checkValid(ConfigFactory.defaultReference())

  private val dpuUriString = config.getString("dpu.uri")
  private val dpuUrl = new URI(dpuUriString).toURL

  val dpu = deserialize(dpuUrl)

  val executionPath = config.getString("dpu.executionpath")

  val parameters = config.hasPath("dpu.parameters") match {
    case false => Map()
    case true => config.getConfig("dpu.parameters").entrySet
      .map(e => (e.getKey -> e.getValue.render)).toMap
  }
}

object LaunchConfiguration {

  /**
   * For applications using application.{conf,json,properties}, system properties can be used to force a different config source:
   *
   * config.resource specifies a resource name - not a basename, i.e. application.conf not application
   * config.file specifies a filesystem path, again it should include the extension, not be a basename
   * config.url specifies a URL
   *
   */
  lazy val APPLICATION_CONF_RESOURCE = "config.resource"
  lazy val APPLICATION_CONF_FILE = "config.file"
  lazy val APPLICATION_CONF_URL = "config.url"

  lazy val VM_ARGUMENTS_KEY = "org.eclipse.jdt.launching.VM_ARGUMENTS";
  lazy val PROGRAM_ARGUMENTS_KEY = "org.eclipse.jdt.launching.PROGRAM_ARGUMENTS";
  lazy val SOURCE_PATH_PROVIDER_KEY = "org.eclipse.jdt.launching.SOURCE_PATH_PROVIDER";

  lazy val SOURCE_PATH_PROVIDER = "org.eclipse.pde.ui.workbenchClasspathProvider"
  lazy val VM_ARGUMENTS = "-Declipse.ignoreApp=true -Dosgi.noShutdown=true"
  lazy val PROGRAM_ARGUMENTS = "-os ${target.os} -ws ${target.ws} -arch ${target.arch} -nl ${target.nl} -consoleLog -console"

  val REQUIRED_BUNDLES = List(
    // Logging
    "ch.qos.logback.classic@default:default",
    "ch.qos.logback.core@default:default",
    "ch.qos.logback.slf4j@default:default",

    // Typesafe 
    "com.typesafe.config@default:default",
    "com.typesafe.akka.actor@default:default",
    "com.typesafe.akka.slf4j@default:default",

    // Knowing stuff
    "de.lmu.ifi.dbs.knowing.core@default:true",
    "de.lmu.ifi.dbs.knowing.debug.presenter@default:true",
    "de.lmu.ifi.dbs.knowing.debug.launcher@5:true",
    "de.lmu.ifi.dbs.knowing.launcher@default:false",
    "de.lmu.ifi.dbs.knowing.presenter@default:default",
    "nz.ac.waikato.cs.weka@default:default",

    // Console
    "org.apache.felix.gogo.command@default:default",
    "org.apache.felix.gogo.runtime@default:default",
    "org.apache.felix.gogo.shell@default:default",

    // Equinox
    "org.eclipse.core.contenttype@default:default",
    "org.eclipse.core.expressions@default:default",
    "org.eclipse.core.filesystem@default:default",
    "org.eclipse.core.jobs@default:default",
    "org.eclipse.core.resources@default:default",
    "org.eclipse.core.runtime@default:true",
    "org.eclipse.core.variables@default:default",
    "org.eclipse.equinox.app@default:default",
    "org.eclipse.equinox.common@2:true",
    "org.eclipse.equinox.console@default:default",
    "org.eclipse.equinox.ds@1:true",
    "org.eclipse.equinox.preferences@default:default",
    "org.eclipse.equinox.registry@default:default",
    "org.eclipse.equinox.util@default:default",
    "org.eclipse.osgi.services@default:default",
    "org.eclipse.osgi@-1:true",

    // Sapphire
    "org.eclipse.sapphire.java@default:default",
    "org.eclipse.sapphire.modeling.xml@default:default",
    "org.eclipse.sapphire.modeling@default:default",
    "org.eclipse.sapphire.osgi.fragment@default:false",
    "org.eclipse.sapphire.osgi@default:default",
    "org.eclipse.sapphire.platform@default:default",
    "org.eclipse.sapphire.workspace@default:default",
    "org.eclipse.wst.common.uriresolver@default:default",

    // 
    "org.scala-ide.scala.library@default:default",
    "org.slf4j.api@default:default")

  def getRequiredBundles(): JList[String] = REQUIRED_BUNDLES

}