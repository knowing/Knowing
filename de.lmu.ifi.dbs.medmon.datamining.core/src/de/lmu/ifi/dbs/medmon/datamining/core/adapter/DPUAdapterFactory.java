package de.lmu.ifi.dbs.medmon.datamining.core.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.medmon.datamining.core.Activator;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;

public class DPUAdapterFactory implements IAdapterFactory {

	private static final Class[] types = new Class[] { IWorkbenchAdapter.class };

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(!(adaptableObject instanceof DataProcessingUnit)) 
			return null;
		
		DataProcessingUnit dpu = (DataProcessingUnit) adaptableObject;
		if(adapterType.equals(IWorkbenchAdapter.class)) {
			return new IWorkbenchAdapter() {
				
				@Override
				public Object getParent(Object o) {
					return null;
				}
				
				@Override
				public String getLabel(Object o) {
					return ((DataProcessingUnit) o).getName();
				}
				
				@Override
				public ImageDescriptor getImageDescriptor(Object object) {
					return Activator.getImageDescriptor("icons/dpu_16.png");
				}
				
				@Override
				public Object[] getChildren(Object o) {
					//TODO return Parameter as Children
					return null;
				}
			};
		}
		
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return types ;
	}

}
