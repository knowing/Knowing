package de.lmu.ifi.dbs.knowing.core.swt.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.lmu.ifi.dbs.knowing.core.factory.IPresenterFactory;
import de.lmu.ifi.dbs.knowing.core.swt.factory.MultiTablePresenterFactory;
import de.lmu.ifi.dbs.knowing.core.swt.factory.TablePresenterFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.lmu.ifi.dbs.knowing.core.swt"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	//
	private ServiceRegistration tablePresenterService;
	private ServiceRegistration multiTablePresenterService;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		tablePresenterService = context.registerService(IPresenterFactory.class.getName(), new TablePresenterFactory(),
				null);
		multiTablePresenterService = context.registerService(IPresenterFactory.class.getName(), new MultiTablePresenterFactory(),
				null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		tablePresenterService.unregister();
		multiTablePresenterService.unregister();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
