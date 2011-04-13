package de.lmu.ifi.dbs.knowing.core.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import akka.actor.ActorRef;
import akka.actor.Actors;
import de.lmu.ifi.dbs.knowing.core.test.MyActor;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		ActorRef actor = Actors.actorOf(MyActor.class).start();
		actor.sendOneWay("Hello You");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	


}
