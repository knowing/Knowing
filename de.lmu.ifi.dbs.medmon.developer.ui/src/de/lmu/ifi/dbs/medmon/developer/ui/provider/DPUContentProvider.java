package de.lmu.ifi.dbs.medmon.developer.ui.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;

public class DPUContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		DataProcessingUnit dpu = (DataProcessingUnit) inputElement;	                                           
		return dpu.toArray();
	}

}
