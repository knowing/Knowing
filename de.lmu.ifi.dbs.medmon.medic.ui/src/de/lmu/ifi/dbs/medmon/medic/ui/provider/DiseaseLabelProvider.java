package de.lmu.ifi.dbs.medmon.medic.ui.provider;

import org.eclipse.jface.viewers.LabelProvider;

import de.lmu.ifi.dbs.medmon.medic.core.extensions.IDisease;

public class DiseaseLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		IDisease disease = (IDisease)element;
		return disease.getName();
	}


}
