package de.lmu.ifi.dbs.knowing.core.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.lmu.ifi.dbs.knowing.core.test.Tester;

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
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Thread started");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Thread woke up");
				Tester tester = new Tester();
				tester.test();
			}
		}).start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	


}
