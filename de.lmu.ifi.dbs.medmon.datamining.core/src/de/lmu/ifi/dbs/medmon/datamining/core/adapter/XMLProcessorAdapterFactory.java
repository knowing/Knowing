package de.lmu.ifi.dbs.medmon.datamining.core.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.property.DataProcessorElement;

public class XMLProcessorAdapterFactory implements IAdapterFactory {

	private static final Class[] types = { IPropertySource.class, IWorkbenchAdapter.class };
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		
		if(!(adaptableObject instanceof XMLDataProcessor))
			return null;
		
		if(adapterType.equals(IPropertySource.class)) {
			return new DataProcessorElement((XMLDataProcessor) adaptableObject);
		} else if (adapterType.equals(IWorkbenchAdapter.class)) {
			return new ProcessorWorkbenchAdapater((XMLDataProcessor)adaptableObject);
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return types;
	}

}
