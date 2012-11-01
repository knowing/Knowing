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
package de.lmu.ifi.dbs.knowing.debug.launcher

import org.osgi.framework.{ BundleContext, BundleActivator }
import de.lmu.ifi.dbs.knowing.debug.presenter.DebugUIFactory
import de.lmu.ifi.dbs.knowing.launcher.LaunchConfiguration
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil
import de.lmu.ifi.dbs.knowing.core.service.IEvaluateService
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.exceptions.ValidationException
import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorSystem, TypedProps, TypedActor }
import java.net.URI
import java.nio.file.Paths
import org.slf4j.LoggerFactory
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.factory.TFactory

/**
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-04-19
 */
class Activator extends BundleActivator {

    val log = LoggerFactory.getLogger(Activator.PLUGIN_ID)

    def start(context: BundleContext) = {

        val configFile = System.getProperty(LaunchConfiguration.APPLICATION_CONF_FILE)
        log.debug("Found config.file property " + configFile)
        listFactories(context)

        val config = ConfigFactory.load()
        val launchConfig = new LaunchConfiguration(config)
        val dpu = launchConfig.dpu
        val reference = Option(context.getServiceReference(classOf[IEvaluateService]))
        if (reference.isDefined) {
            try {
                val evaluateService = context.getService(reference.get)
                //Only use default ActorSystem()
                val name = "debug-system" + System.currentTimeMillis
                val uiFactory = TypedActor(ActorSystem(name, config, classOf[ActorSystem].getClassLoader))
                    .typedActorOf(TypedProps(classOf[UIFactory[_]], new DebugUIFactory(launchConfig.executionPath)))

                log.info("Starting evaluation on ActorSystem " + name)
                evaluateService.evaluate(dpu, Paths.get(launchConfig.executionPath).toUri, uiFactory, null, null, null, null)
            } catch {
                case e: ValidationException => log.error(e.getErrors.toString);
            }
        }
    }

    def stop(context: BundleContext) = {
        log.info("Stopping DebugLauncher Bundle")
    }

    private def listFactories(context: BundleContext) = {
        val reference = Option(context.getServiceReference(classOf[IFactoryDirectory]))
        if (reference.isDefined) {
            val factoryDir = context.getService(reference.get)
            log.debug("### Available factories: ")
            factoryDir.getFactories
                .sortWith((f1, f2) => (f1.id.compareToIgnoreCase(f2.id)) > 0)
                .foreach(f => log.debug("  # " + f.id))
        }
    }

}

object Activator {
    val PLUGIN_ID = "de.lmu.ifi.dbs.knowing.debug.launcher"
}