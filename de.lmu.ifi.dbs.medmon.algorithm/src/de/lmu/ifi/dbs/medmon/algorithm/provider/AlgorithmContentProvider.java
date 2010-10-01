package de.lmu.ifi.dbs.medmon.algorithm.provider;

import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;

/**
 * Needs no specified input element. Provides all registered 
 * ISensorDataAlgorithm Extensions.
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 */
public class AlgorithmContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return evaluateAlgorithms();
	}

	/**
	 * Provides all registered ISensorDataAlgorithm Extensions.
	 * 
	 * @return ISensorDataAlgorihtm[] containing all registered Extensions
	 */
	public static ISensorDataAlgorithm[] evaluateAlgorithms() {
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

}
