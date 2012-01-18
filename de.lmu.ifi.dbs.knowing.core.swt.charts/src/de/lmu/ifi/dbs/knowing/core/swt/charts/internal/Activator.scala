package de.lmu.ifi.dbs.knowing.core.swt.charts.internal

import de.lmu.ifi.dbs.knowing.core.swt.charts._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import org.osgi.framework.{ BundleContext, BundleActivator, ServiceRegistration }

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 25.04.2011
 *
 */
class Activator extends BundleActivator {

  var services: List[ServiceRegistration[_]] = Nil

  def start(context: BundleContext) = {
    services = context.registerService(classOf[TFactory].getName, new PieChartPresenterFactory, null) :: services
    services = context.registerService(classOf[TFactory].getName, new TimeIntervalClassPresenterFactory, null) :: services
    services = context.registerService(classOf[TFactory].getName, new TimeSeriesPresenterFactory, null) :: services
  }

  def stop(context: BundleContext) = {
    services foreach (registration => registration.unregister)
  }

}