package de.lmu.ifi.dbs.medmon.sensor.core.util;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;

import de.lmu.ifi.dbs.medmon.sensor.core.Activator;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.IDataProcessor;

public class FrameworkUtil {

	private static final Logger logger = Logger.getLogger(Activator.PLUGIN_ID);  
	
	public static IDataProcessor[] evaluateDataProcessors() {
		Object[] processors = FrameworkUtil.<IDataProcessor>evaluateExtensions(IDataProcessor.PROCESSOR_ID);
		IDataProcessor[] returns = new IDataProcessor[processors.length];
		for (int i = 0; i < returns.length; i++) 
			returns[i] = (IDataProcessor) processors[i];
		return returns;
	}
	
	/**
	 * Provides all registered ISensorDataAlgorithm Extensions.
	 * No TypeCast check is made!
	 * 
	 * @return ISensorDataAlgorihtm[] containing all registered Extensions
	 */
	public static <E> E[] evaluateExtensions(String extensionID) {
		logger.info("Evaluate Extensions");
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionID);
		final LinkedList<E> extensions = new LinkedList<E>();
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				//E castCheck = (E)o;
				
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
			logger.severe(ex.getMessage());
		}
		E[] returns = (E[]) new Object[extensions.size()];
		return extensions.toArray(returns);
	}
	
	public static IAlgorithm findAlgorithm(String name) {
		IDataProcessor[] algorithms = evaluateDataProcessors();
		for(IDataProcessor algorithm : algorithms) {
			if(algorithm instanceof IAlgorithm && algorithm.getName().equals(name))
				return (IAlgorithm) algorithm;
		}
		return null;
	}
	
}
