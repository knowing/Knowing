package de.lmu.ifi.dbs.knowing.debug.core.internal;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import akka.actor.TypedActor;
import akka.actor.TypedActorFactory;

import de.lmu.ifi.dbs.knowing.core.factory.UIFactory;

public class Activator implements BundleActivator {

	public static final String PLUGIN_ID = "de.lmu.ifi.dbs.knowing.debug.core";
	
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
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
