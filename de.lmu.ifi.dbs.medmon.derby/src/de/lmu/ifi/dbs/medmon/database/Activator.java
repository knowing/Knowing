package de.lmu.ifi.dbs.medmon.database;

import javax.persistence.EntityManagerFactory;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
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

	private ServiceTracker emfbTracker;

	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		database = new Database();
		emfTracker = new ServiceTracker(context, EntityManagerFactory.class.getName(),
				new JPAServiceTrackerCustomizer());
		emfTracker.open();

		emfbTracker = new ServiceTracker(context, EntityManagerFactoryBuilder.class.getName(),
				JPAUtil.getServiceTrackerCustomizer("medmon", database.getProperties()));
		emfbTracker.open();
		startingDBBundles(context);
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
		emfbTracker.close();
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

	private void startingDBBundles(BundleContext context) {
		Bundle[] bundles = context.getBundles();
		final String jpa = "org.eclipse.gemini.jpa";
		for (final Bundle bundle : bundles) {
			if (jpa.equals(bundle.getSymbolicName())) {
				Runnable startJPA = new Runnable() {

					@Override
					public void run() {
						try {
							System.out.println("##### Trying to start " + jpa);
							bundle.start();
							System.out.println("##### Started!");
						} catch (BundleException e) {
							e.printStackTrace();
						}
					}
				};
				Thread thread = new Thread(startJPA);
				thread.start();
				
			}
		}

	}

}
