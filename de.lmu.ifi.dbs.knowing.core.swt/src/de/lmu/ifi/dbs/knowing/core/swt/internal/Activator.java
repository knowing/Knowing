/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.lmu.ifi.dbs.knowing.core.factory.IPresenterFactory;
import de.lmu.ifi.dbs.knowing.core.swt.TablePresenterFactory;

/**
 * @author muki
 * @version 0.1
 * @since 21.03.2011
 */
public class Activator implements BundleActivator {

	private static BundleContext context;
	private ServiceRegistration tablePresenterService;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		tablePresenterService = context.registerService(IPresenterFactory.class.getName(), new TablePresenterFactory(),
				null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		tablePresenterService.unregister();
		Activator.context = null;
	}
}
