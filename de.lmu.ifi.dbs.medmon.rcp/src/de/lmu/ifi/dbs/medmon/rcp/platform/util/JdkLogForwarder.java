package de.lmu.ifi.dbs.medmon.rcp.platform.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class JdkLogForwarder {
	
	protected static final String LOG_SERVICE_CLASS_NAME = "org.osgi.service.log.LogService";
	
	private final BundleContext bundleContext;
	private final String[] loggerNames;
	private final Handler defaultHandler;
	private final ServiceListener logServiceServiceListener = new LogServiceServiceListener();
	
	private Handler lastUsedHandler;
	private ServiceReference lastUsedLogServiceRef;
	
	public JdkLogForwarder(BundleContext bundleContext, String[] loggerNames, Handler defaultHandler) {
		this.bundleContext = bundleContext;
		this.loggerNames = loggerNames;

		this.defaultHandler = defaultHandler != null ? defaultHandler : new DummyLogHandler();
	}
	
	public JdkLogForwarder(BundleContext bundleContext, String[] loggerNames) {
		this(bundleContext, loggerNames, null);
	}


	public void start() {
		for (String loggerName : getLoggerNames()) {
			Logger logger = Logger.getLogger(loggerName);
			logger.setUseParentHandlers(false);
		}

		updateLogHandler();

		addLogServiceListener();
	}

	public void stop() {
		getBundleContext().removeServiceListener(getLogServiceServiceListener());

		for (String loggerName : getLoggerNames()) {
			Logger.getLogger(loggerName).removeHandler(getLastUsedHandler());
		}

		if (getLastUsedLogServiceRef() != null) {
			getBundleContext().ungetService(getLastUsedLogServiceRef());
		}
	}

	protected void updateLogHandler() {
		ServiceReference logServiceRef = getBundleContext().getServiceReference(LOG_SERVICE_CLASS_NAME);
		Handler logHandler = null;

		if (logServiceRef != null && logServiceRef == getLastUsedLogServiceRef()) {
			return;
		}
		if (logServiceRef != null) {
			logHandler = new OsgiLogDelegateHandler((LogService) getBundleContext().getService(logServiceRef));
		} else {
			logHandler = getDefaultHandler();
		}

		Handler lastUsedHandler = getLastUsedHandler();

		for (String loggerName : getLoggerNames()) {
			Logger logger = Logger.getLogger(loggerName);
			logger.removeHandler(lastUsedHandler);
			logger.addHandler(logHandler);
		}

		if (getLastUsedLogServiceRef() != null) {
			getBundleContext().ungetService(getLastUsedLogServiceRef());
		}

		setLastUsedLogServiceRef(logServiceRef);
		setLastUsedHandler(logHandler);
	}

	protected void addLogServiceListener() {
		try {
			getBundleContext().addServiceListener(getLogServiceServiceListener(),
					String.format("(%s=%s)", Constants.OBJECTCLASS, LOG_SERVICE_CLASS_NAME));
		} catch (InvalidSyntaxException ex) {
			// / This exception should not occur in the first place
			throw new RuntimeException(ex.getMessage());
		}
	}

	protected BundleContext getBundleContext() {
		return bundleContext;
	}

	protected String[] getLoggerNames() {
		return loggerNames;
	}

	protected Handler getDefaultHandler() {
		return defaultHandler;
	}

	protected ServiceListener getLogServiceServiceListener() {
		return logServiceServiceListener;
	}

	protected ServiceReference getLastUsedLogServiceRef() {
		return lastUsedLogServiceRef;
	}

	protected void setLastUsedLogServiceRef(ServiceReference lastUsedLogServiceRef) {
		this.lastUsedLogServiceRef = lastUsedLogServiceRef;
	}

	protected Handler getLastUsedHandler() {
		return lastUsedHandler;
	}

	protected void setLastUsedHandler(Handler lastUsedHandler) {
		this.lastUsedHandler = lastUsedHandler;
	}

	/**
	 * 
	 */
	protected class LogServiceServiceListener implements ServiceListener {
		// @Override
		public void serviceChanged(ServiceEvent serviceEvent) {
			try {
				updateLogHandler();
			} catch (Throwable ex) {
				System.out.println("Error: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	protected class OsgiLogDelegateHandler extends Handler {
		@Override
		public void publish(LogRecord record) {
			if (record.getLevel() == Level.OFF) {
				return;
			}

			if (record.getThrown() != null) {
				getLogService().log(getToOsgiLogLevel(record.getLevel()), record.getMessage(), record.getThrown());
			} else {
				getLogService().log(getToOsgiLogLevel(record.getLevel()), record.getMessage());
			}
		}

		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}

		protected int getToOsgiLogLevel(Level level) {
			if (level == Level.SEVERE) {
				return LogService.LOG_ERROR;
			} else if (level == Level.WARNING) {
				return LogService.LOG_WARNING;
			} else if (level == Level.INFO || level == Level.CONFIG || level == Level.FINE) {
				return LogService.LOG_INFO;
			} else {
				return LogService.LOG_DEBUG;
			}
		}

		public OsgiLogDelegateHandler(LogService logService) {
			this.logService = logService;
		}

		private final LogService logService;

		protected LogService getLogService() {
			return logService;
		}
	}

	/**
	 *
	 */
	protected class DummyLogHandler extends Handler {
		@Override
		public void publish(LogRecord record) {
		}

		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}
	}

}
