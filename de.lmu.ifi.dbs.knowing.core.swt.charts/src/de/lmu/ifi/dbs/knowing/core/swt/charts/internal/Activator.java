package de.lmu.ifi.dbs.knowing.core.swt.charts.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.lmu.ifi.dbs.knowing.core.swt.charts.PiePresenterFactory;
import de.lmu.ifi.dbs.knowing.core.swt.charts.TimePeriodValuesPresenter;
import de.lmu.ifi.dbs.knowing.core.util.FactoryUtil;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		//Registering all factories
		FactoryUtil.registerPresenterFactory(new PiePresenterFactory(), null, bundleContext);
		FactoryUtil.registerPresenterFactory(new TimePeriodValuesPresenter(), null, bundleContext);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}


}
