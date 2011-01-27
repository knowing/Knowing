package de.lmu.ifi.dbs.medmon.database;

import javax.persistence.EntityManagerFactory;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.lmu.ifi.dbs.medmon.database.install.Database;
import de.lmu.ifi.dbs.medmon.database.osgi.JPAServiceTrackerCustomizer;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;

public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.lmu.ifi.dbs.medmon.database"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	// Database
	private static Database database;

	private static ServiceTracker emfTracker;

	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		database = new Database();
		emfTracker = new ServiceTracker(context, EntityManagerFactory.class.getName(),
				new JPAServiceTrackerCustomizer());
		emfTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		emfTracker.close();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static Database getDatabase() {
		return database;
	}
	
	public static ServiceTracker getEmfTracker() {
		return emfTracker;
	}

}
