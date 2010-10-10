package de.lmu.ifi.dbs.medmon.algorithm.util;

import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;

import de.lmu.ifi.dbs.medmon.algorithm.activator.Activator;
import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;

public class AlgorithmUtil {
	
	public static ISensorDataAlgorithm[] evaluateAlgorithms() {
		ISensorDataAlgorithm[] extensions = evaluateAlgorithmsExtensions();
		ISensorDataAlgorithm[] services = evaluateAlgorithmServices();
		return merge(extensions, services);
	}
		
	/**
	 * Provides all registered ISensorDataAlgorithm Extensions.
	 * 
	 * @return ISensorDataAlgorihtm[] containing all registered Extensions
	 */
	public static ISensorDataAlgorithm[] evaluateAlgorithmsExtensions() {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ISensorDataAlgorithm.ALGORITHM_ID);
		final LinkedList<ISensorDataAlgorithm> algorithms = new LinkedList<ISensorDataAlgorithm>();
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof ISensorDataAlgorithm) {
					ISafeRunnable runnable = new ISafeRunnable() {
						@Override
						public void handleException(Throwable exception) {
							System.out.println("Exception in client");
						}

						@Override
						public void run() throws Exception {
							algorithms.add((ISensorDataAlgorithm) o);
						}
					};
					SafeRunner.run(runnable);
				}
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}
		
		return algorithms.toArray(new ISensorDataAlgorithm[algorithms.size()]);
	}
	
	public static ISensorDataAlgorithm[] evaluateAlgorithmServices() {
		return Activator.getAlgorithmServices();
	}
	
	private static ISensorDataAlgorithm[] merge(ISensorDataAlgorithm[] a, ISensorDataAlgorithm[] b) {
		ISensorDataAlgorithm[] returns;
		if(a != null && b != null) {
			//Real merge 
			returns = new ISensorDataAlgorithm[a.length + b.length];
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
		
		return new ISensorDataAlgorithm[0];
	}
	
	public static ISensorDataAlgorithm findAlgorithm(String name) {
		ISensorDataAlgorithm[] algorithms = evaluateAlgorithms();
		for(ISensorDataAlgorithm algorithm : algorithms) {
			if(algorithm.getName().equals(name))
				return algorithm;
		}
		return null;
	}

}
