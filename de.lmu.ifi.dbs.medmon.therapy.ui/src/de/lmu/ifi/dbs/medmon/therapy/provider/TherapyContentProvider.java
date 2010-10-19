package de.lmu.ifi.dbs.medmon.therapy.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.therapy.core.extensions.IDisease;
import de.lmu.ifi.dbs.medmon.therapy.core.extensions.ITherapy;

public class TherapyContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof IDisease) 
			return ((IDisease)inputElement).getTherapies();
		if(inputElement instanceof ITherapy)
			return new ITherapy[] { (ITherapy)inputElement };
		if(inputElement instanceof ITherapy[] )
			return (ITherapy[])inputElement;
		return new Object[0];
	}

}
