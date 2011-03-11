package de.lmu.ifi.dbs.medmon.datamining.core.util;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import de.lmu.ifi.dbs.medmon.datamining.core.Activator;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

public class FrameworkUtil {

	private static final Logger logger = Logger.getLogger(Activator.PLUGIN_ID);

	/**
	 * Provides all registered ISensorDataAlgorithm Extensions. No TypeCast
	 * check is made!
	 * 
	 * @return ISensorDataAlgorihtm[] containing all registered Extensions
	 */
	public static <E> E[] evaluateExtensions(String extensionID) {
		//   public <T> T find(Class<T> entityClass, Object primaryKey); for instanceof check
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionID);
		final LinkedList<E> extensions = new LinkedList<E>();
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				// E castCheck = (E)o;

				ISafeRunnable runnable = new ISafeRunnable() {
					@Override
					public void handleException(Throwable exception) {
						logger.severe("Exception in client");
					}

					@Override
					public void run() throws Exception {
						extensions.add((E) o);
					}
				};
				SafeRunner.run(runnable);

			}
		} catch (CoreException ex) {
			ex.printStackTrace();
			logger.severe(ex.getMessage());
		}
		E[] returns = (E[]) new Object[extensions.size()];
		return extensions.toArray(returns);
	}
	
	/**
	 * Provides all registered ISensorDataAlgorithm Extensions. No TypeCast
	 * check is made!
	 * 
	 * @return ISensorDataAlgorihtm[] containing all registered Extensions
	 */
	public static <E> List<E> evaluateExtensionsAsList(String extensionID) {
		//   public <T> T find(Class<T> entityClass, Object primaryKey); for instanceof check
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionID);
		final LinkedList<E> extensions = new LinkedList<E>();
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				// E castCheck = (E)o;

				ISafeRunnable runnable = new ISafeRunnable() {
					@Override
					public void handleException(Throwable exception) {
						logger.severe("Exception in client");
					}

					@Override
					public void run() throws Exception {
						extensions.add((E) o);
					}
				};
				SafeRunner.run(runnable);

			}
		} catch (CoreException ex) {
			ex.printStackTrace();
			logger.severe(ex.getMessage());
		}
		return extensions;
	}

	public static <E> E[] evaluateService(String clazz) {
		BundleContext context = Activator.getDefault().getBundle().getBundleContext();
		final LinkedList<E> services = new LinkedList<E>();
		try {
			ServiceReference[] serviceReferences = context.getServiceReferences(clazz, null);
			if(serviceReferences == null)
				return null;
			for (ServiceReference serviceReference : serviceReferences) {
				if (serviceReference == null)
					continue;
				Object service = context.getService(serviceReference);
				if (service != null)
					services.add((E) service);
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		E[] returns = (E[]) new Object[services.size()];
		return services.toArray(returns);
	}

	public static IAlgorithm findAlgorithm(String name) {
		IDataProcessor[] algorithms = evaluateDataProcessors();
		for (IDataProcessor algorithm : algorithms) {
			if (algorithm instanceof IAlgorithm && algorithm.getName().equals(name))
				return (IAlgorithm) algorithm;
		}

		return null;
	}

	public static IDataProcessor findDataProcessor(String id) {
		logger.info("Find DataProcessor: " + id);
		// Check Extension Points
		IDataProcessor[] processors = evaluateDataProcessors();
		for (IDataProcessor iDataProcessor : processors) {
			if (iDataProcessor.getId().equals(id))
				return iDataProcessor;
		}
		// Check registered Services
		processors = evaluateService(IDataProcessor.class.getName());
		if(processors == null)
			return null;
		for (IDataProcessor iDataProcessor : processors) {
			if (iDataProcessor.getId().equals(id))
				return iDataProcessor;
		}
		return null;
	}

	public static IDataProcessor[] evaluateDataProcessors() {
		Object[] processors = FrameworkUtil.<IDataProcessor> evaluateExtensions(IDataProcessor.PROCESSOR_ID);
		IDataProcessor[] returns = new IDataProcessor[processors.length];
		for (int i = 0; i < returns.length; i++)
			returns[i] = (IDataProcessor) processors[i];
		return returns;
	}
	

}
