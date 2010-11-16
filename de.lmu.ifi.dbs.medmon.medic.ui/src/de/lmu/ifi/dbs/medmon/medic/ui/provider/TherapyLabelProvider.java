package de.lmu.ifi.dbs.medmon.medic.ui.provider;

import org.eclipse.jface.viewers.LabelProvider;

import de.lmu.ifi.dbs.medmon.medic.core.extensions.ITherapy;

public class TherapyLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		ITherapy therapy = (ITherapy)element;
		return therapy.getName();
	}

}
