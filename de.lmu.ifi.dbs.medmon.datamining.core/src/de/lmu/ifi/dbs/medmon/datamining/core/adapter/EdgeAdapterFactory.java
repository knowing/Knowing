package de.lmu.ifi.dbs.medmon.datamining.core.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.graph.Edge;

public class EdgeAdapterFactory implements IAdapterFactory {

	private static final Class[] types = { IWorkbenchAdapter.class };
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType.equals(IWorkbenchAdapter.class)) {
			return new IWorkbenchAdapter() {
				
				@Override
				public Object getParent(Object o) {
					return null;
				}
				
				@Override
				public String getLabel(Object o) {
					return ((Edge)o).toString();
				}
				
				@Override
				public ImageDescriptor getImageDescriptor(Object object) {
					return null;
				}
				
				@Override
				public Object[] getChildren(Object o) {
					return new Object[0];
				}
			};
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return types;
	}

}
