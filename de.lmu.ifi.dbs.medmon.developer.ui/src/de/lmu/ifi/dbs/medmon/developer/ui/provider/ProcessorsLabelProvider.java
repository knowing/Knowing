package de.lmu.ifi.dbs.medmon.developer.ui.provider;

import org.eclipse.jface.viewers.LabelProvider;

import de.lmu.ifi.dbs.medmon.sensor.core.processing.IDataProcessor;

public class ProcessorsLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof IDataProcessor)
			return ((IDataProcessor)element).getName();
		return "Unkown Type";
	}
	
}
