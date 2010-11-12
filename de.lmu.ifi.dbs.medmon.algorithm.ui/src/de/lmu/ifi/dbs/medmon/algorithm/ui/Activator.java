package de.lmu.ifi.dbs.medmon.algorithm.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.lmu.ifi.dbs.medmon.algorithm.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private static ServiceTracker patientTracker;
	private static ServiceTracker algorithmTracker;
	
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
		
		patientTracker = new ServiceTracker(context, IPatientService.class.getName(), null);
		patientTracker.open();
		
		algorithmTracker = new ServiceTracker(context, IAlgorithm.class.getName(), null);
		algorithmTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		patientTracker.close();
		algorithmTracker.close();
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
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static IPatientService getPatientService() {
		return (IPatientService) patientTracker.getService();
	}
	
	public static IAlgorithm[] getAlgorithmServices() {
		return (IAlgorithm[]) algorithmTracker.getServices();
	}
}
