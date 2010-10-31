package de.lmu.ifi.dbs.medmon.therapy.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.sensor.core.util.FrameworkUtil;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.IDisease;

public class DiseaseContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return FrameworkUtil.<IDisease>evaluateExtensions(IDisease.DISEASE_ID);
	}

}
