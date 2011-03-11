package de.lmu.ifi.dbs.medmon.developer.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.medmon.developer.ui.graph.Edges;

public class EdgesAdapterFactory implements IAdapterFactory {

	private static final Class[] clazz = new Class[] { IWorkbenchAdapter.class };
	
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
					return o.toString();
				}
				
				@Override
				public ImageDescriptor getImageDescriptor(Object object) {
					return null;
				}
				
				@Override
				public Object[] getChildren(Object o) {
					return ((Edges)o).toArray();
				}
			};
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return clazz;
	}

}
