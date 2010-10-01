package de.lmu.ifi.dbs.medmon.therapy.provider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.therapy.IDisease;
import de.lmu.ifi.dbs.medmon.therapy.activator.Activator;

public class DiseaseContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<IDisease> returns = new LinkedList<IDisease>();
		returns.addAll(evaluateExtensions());
		returns.addAll(evaluateServices());
		return returns.toArray(new IDisease[returns.size()]);
	}

	private List<IDisease> evaluateServices() {
		IDisease[] diseases = Activator.getIDiseaseServices();
		ArrayList<IDisease> returns = new ArrayList<IDisease>(diseases.length + 4);
		for(IDisease disease : diseases)
			returns.add(disease);
		return returns;
	}

	private List<IDisease> evaluateExtensions() {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(IDisease.DISEASE_ID);
		final LinkedList<IDisease> returns = new LinkedList<IDisease>();
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
							returns.add((IDisease) o);
						}
					};
					SafeRunner.run(runnable);
				}
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}
		return returns;
	}

}
