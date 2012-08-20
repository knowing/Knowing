package de.lmu.ifi.dbs.knowing.core.swt.charts.internal

import de.lmu.ifi.dbs.knowing.core.swt.charts._
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil
import org.osgi.framework.{ BundleContext, BundleActivator, ServiceRegistration }

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 25.04.2011
 *
 */
class Activator extends BundleActivator {

	private var osgi: OSGIUtil = _

	def start(context: BundleContext) = {
		osgi = new OSGIUtil(context)
		osgi.registerPresenter(new PieChartPresenterFactory)
		osgi.registerPresenter(new TimeIntervalClassPresenterFactory)
		osgi.registerPresenter(new TimeSeriesPresenterFactory)
		osgi.registerPresenter(new AreaChartPresenterFactory)
	}

	def stop(context: BundleContext) = {
		osgi.deregisterAll
		osgi = null
	}

}