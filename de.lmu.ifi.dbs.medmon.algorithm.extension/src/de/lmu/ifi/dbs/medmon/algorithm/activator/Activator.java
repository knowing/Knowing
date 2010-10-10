package de.lmu.ifi.dbs.medmon.algorithm.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;

public class Activator implements BundleActivator {

	private static ServiceTracker algorithmTracker; 
	
	@Override
	public void start(BundleContext context) throws Exception {
		algorithmTracker = new ServiceTracker(context, ISensorDataAlgorithm.class.getName(), null);
		algorithmTracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		algorithmTracker.close();
	}
	
	public static ISensorDataAlgorithm[] getAlgorithmServices() {
		return (ISensorDataAlgorithm[]) algorithmTracker.getServices();
	}

}
