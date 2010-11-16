package de.lmu.ifi.dbs.medmon.medic.core.util;

import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;

import de.lmu.ifi.dbs.medmon.medic.core.Activator;
import de.lmu.ifi.dbs.medmon.medic.core.extensions.IDisease;


public class DiseaseUtil {

	public static IDisease[] evaluateDiseases() {
		IDisease[] extensions = evaluateDiseaseExtensions();
		IDisease[] services = evaluateDiseaseServices();
		return merge(extensions, services);
	}
	
	private static IDisease[] merge(IDisease[] a, IDisease[] b) {
		IDisease[] returns;
		if(a != null && b != null) {
			//Real merge 
			returns = new IDisease[a.length + b.length];
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
		
		return new IDisease[0];
	}
	
	/**
	 * Provides all registered ISensorDataAlgorithm Extensions.
	 * 
	 * @return ISensorDataAlgorihtm[] containing all registered Extensions
	 */
	public static IDisease[] evaluateDiseaseExtensions() {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(IDisease.DISEASE_ID);
		final LinkedList<IDisease> algorithms = new LinkedList<IDisease>();
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof IDisease) {
					ISafeRunnable runnable = new ISafeRunnable() {
						@Override
						public void handleException(Throwable exception) {
							System.out.println("Exception in client");
						}

						@Override
						public void run() throws Exception {
							algorithms.add((IDisease) o);
						}
					};
					SafeRunner.run(runnable);
				}
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}
		return algorithms.toArray(new IDisease[algorithms.size()]);
	}
	
	public static IDisease[] evaluateDiseaseServices() {
		return Activator.getIDiseaseServices();
	}
}
