package de.lmu.ifi.dbs.medmon.rcp;

import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

import de.lmu.ifi.dbs.medmon.rcp.platform.logging.LogsPublisher;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.JdkLogForwarder;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.lmu.ifi.dbs.medmon.rcp"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private JdkLogForwarder jdkLogForwarder;
	
	private LogsPublisher logsPublisher = new LogsPublisher();
	
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
		
		/// Without a log reader we will not see anything on the screen even if
		/// logs were published through OSGi Log Service 
		addLogReader(context);
		
		/// The JDK Log Forwarder takes a default log handler as an argument which
		/// will be used in the case the OSGi Log Service was not available.
		/// The handle argument is optional.
		ConsoleHandler defaultHandler = new ConsoleHandler();
		defaultHandler.setFormatter(new SimpleFormatter());
		
		/// Other than the default handler we pass the package name of the
		/// classes whose JDK loggers must forward logs to the OSGi Service.
		jdkLogForwarder = new JdkLogForwarder(context, new String[] { "de.lmu.ifi.dbs.medmon.logger" },	defaultHandler);
		
		/// We start the Log Forwarder
		jdkLogForwarder.start();
		
		/// We start the publisher which publishes logs every 3 seconds.
		logsPublisher.start();
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		logsPublisher.stop();
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
	
	
	//Logging
	
	protected void addLogReader(final BundleContext bundleContext) {
		new Thread(new Runnable() {
			public void run() {
				sleep(1000);
				
				((LogReaderService)bundleContext.getService(
						bundleContext.getServiceReference(LogReaderService.class.getName()))).addLogListener(
						new LogListener() {
							public void logged(LogEntry entry) {
								System.out.println(entry.getLevel() + ": " + entry.getMessage());
							}
						});
			}
		}).start();
	}
	
	protected void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception ex) {
			return;
		}
	}
	

	
}
