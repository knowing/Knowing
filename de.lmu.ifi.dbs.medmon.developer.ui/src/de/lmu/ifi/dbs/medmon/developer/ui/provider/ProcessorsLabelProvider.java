package de.lmu.ifi.dbs.medmon.developer.ui.provider;

import org.eclipse.jface.viewers.LabelProvider;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

public class ProcessorsLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof XMLDataProcessor)
			return ((XMLDataProcessor)element).getName();
		if(element instanceof IDataProcessor)
			return ((IDataProcessor)element).getName();
		return "Unkown Type";
	}
	
}
