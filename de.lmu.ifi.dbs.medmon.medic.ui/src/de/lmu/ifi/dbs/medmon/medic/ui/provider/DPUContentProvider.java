package de.lmu.ifi.dbs.medmon.medic.ui.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;

/**
 * This provider structures the DPU in categories and gives access to the
 * parameters via TreeElements
 * @author Nepomuk Seiler
 * @version 0.1
 */
public class DPUContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof DataProcessingUnit[])
			return (DataProcessingUnit[])inputElement;
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}
	
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	
	}

}
