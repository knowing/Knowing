package de.lmu.ifi.dbs.medmon.therapy.provider;

import org.eclipse.jface.viewers.LabelProvider;

import de.lmu.ifi.dbs.medmon.therapy.core.extensions.ITherapy;

public class TherapyLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		ITherapy therapy = (ITherapy)element;
		return therapy.getName();
	}

}
