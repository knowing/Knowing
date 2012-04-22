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
package de.lmu.ifi.dbs.knowing.debug.presenter

import de.lmu.ifi.dbs.knowing.presenter._
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import java.nio.file.Path
import org.osgi.framework.{ServiceRegistration,BundleContext}
import scala.collection.mutable.ListBuffer

/**
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2011-04-22
 *
 */
object DebugPresenterFactories {

	val registrations = new ListBuffer[ServiceRegistration[_]]
	
	def registerAll(context: BundleContext) {
		registrations += register(new DebugPresenterPieChartFactory, context)
		registrations += register(new DebugPresenterTableFactory, context)
		registrations += register(new DebugPresenterTimeIntervalFactory, context)
		registrations += register(new DebugPresenterTimeSeriesFactory, context)
	}
	
	def unregisterAll() = registrations foreach(_.unregister)
	
	def register(factory: TFactory,context: BundleContext): ServiceRegistration[_] = {
		val properties = OSGIUtil.createServiceProperties(factory)
		context.registerService(classOf[TFactory], factory, properties)
	}
}

class DebugPresenterPieChartFactory extends PresenterFactory(classOf[DebugPresenter], classOf[IPieChartPresenter[Path]])

class DebugPresenterTableFactory extends PresenterFactory(classOf[DebugPresenter], classOf[ITablePresenter[Path]])

class DebugPresenterTimeIntervalFactory extends PresenterFactory(classOf[DebugPresenter], classOf[ITimeIntervalClassPresenter[Path]])

class DebugPresenterTimeSeriesFactory extends PresenterFactory(classOf[DebugPresenter], classOf[ITimeSeriesPresenter[Path]])