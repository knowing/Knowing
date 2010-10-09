package de.lmu.ifi.dbs.medmon.therapy.provider;

import org.eclipse.jface.viewers.LabelProvider;

import de.lmu.ifi.dbs.medmon.therapy.IDisease;

public class DiseaseLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		IDisease disease = (IDisease)element;
		return disease.getName();
	}


}
