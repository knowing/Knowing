package de.lmu.ifi.dbs.medmon.datamining.core.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.property.DataProcessorElement;

public class ProcessParameterAdapterFactory implements IAdapterFactory {

	private static final Class[] types = { IPropertySource.class };
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		System.out.println("ProcessParameterAdapterFactory.getAdapter()");
		if(adapterType.equals(IPropertySource.class) && adaptableObject instanceof DataProcessor) {
			return new DataProcessorElement((DataProcessor) adaptableObject);
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		System.out.println("ProcessParameterAdapterFactory.getAdapterList()");
		return types;
	}

}
