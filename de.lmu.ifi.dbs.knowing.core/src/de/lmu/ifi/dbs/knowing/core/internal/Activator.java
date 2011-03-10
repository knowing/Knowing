package de.lmu.ifi.dbs.knowing.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.lmu.ifi.dbs.knowing.core.graph.Edge;
import de.lmu.ifi.dbs.knowing.core.graph.GraphSupervisor;
import de.lmu.ifi.dbs.knowing.core.graph.INode;
import de.lmu.ifi.dbs.knowing.core.graph.InputNode;
import de.lmu.ifi.dbs.knowing.core.graph.ProcessorNode;

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
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	


}
