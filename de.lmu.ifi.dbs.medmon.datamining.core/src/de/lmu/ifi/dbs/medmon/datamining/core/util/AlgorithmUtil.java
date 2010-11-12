package de.lmu.ifi.dbs.medmon.datamining.core.util;

import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

public class AlgorithmUtil {
	
	public static IAlgorithm[] evaluateAlgorithms() {
		IAlgorithm[] extensions = evaluateAlgorithmsExtensions();
		//IAlgorithm[] services = evaluateAlgorithmServices();
		//return merge(extensions, services);
		return extensions;
	}
		
	/**
	 * Provides all registered ISensorDataAlgorithm Extensions.
	 * 
	 * @return ISensorDataAlgorihtm[] containing all registered Extensions
	 */
	public static IAlgorithm[] evaluateAlgorithmsExtensions() {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(IDataProcessor.PROCESSOR_ID);
		final LinkedList<IAlgorithm> algorithms = new LinkedList<IAlgorithm>();
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof IAlgorithm) {
					ISafeRunnable runnable = new ISafeRunnable() {
						@Override
						public void handleException(Throwable exception) {
							System.out.println("Exception in client");
						}

						@Override
						public void run() throws Exception {
							algorithms.add((IAlgorithm) o);
						}
					};
					SafeRunner.run(runnable);
				}
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}
		
		return algorithms.toArray(new IAlgorithm[algorithms.size()]);
	}
	
	/*public static IAlgorithm[] evaluateAlgorithmServices() {
		return Activator.getAlgorithmServices();
	}*/
	
	private static IAlgorithm[] merge(IAlgorithm[] a, IAlgorithm[] b) {
		IAlgorithm[] returns;
		if(a != null && b != null) {
			//Real merge 
			returns = new IAlgorithm[a.length + b.length];
			int index = 0;
			for(int i=0; i < a.length; i++)
				returns[index++] = a[i];
			for(int i=0; i < b.length; i++)
				returns[index++] = b[i];
		} else if(a != null) {
			return a;
		} else if(b != null) {
			return b;
		}
		
		return new IAlgorithm[0];
	}
	
	public static IAlgorithm findAlgorithm(String name) {
		IAlgorithm[] algorithms = evaluateAlgorithms();
		for(IAlgorithm algorithm : algorithms) {
			if(algorithm.getName().equals(name))
				return algorithm;
		}
		return null;
	}

}
