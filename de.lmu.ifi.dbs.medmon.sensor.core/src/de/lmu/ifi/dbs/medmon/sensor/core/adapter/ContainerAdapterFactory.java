package de.lmu.ifi.dbs.medmon.sensor.core.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

public class ContainerAdapterFactory implements IAdapterFactory {

	private static final Class[] types = new Class[] {  ISensorDataContainer.class, IWorkbenchAdapter.class };
	
	@Override
	public Object getAdapter(Object element, Class clazz) {;
		if(clazz.equals(IWorkbenchAdapter.class) && element instanceof ISensorDataContainer) {
			return new IWorkbenchAdapter() {
				
				@Override
				public Object getParent(Object o) {
					return ((ISensorDataContainer)o).getParent();
				}
				
				@Override
				public String getLabel(Object o) {
					return ((ISensorDataContainer)o).getName();
				}
				
				@Override
				public ImageDescriptor getImageDescriptor(Object object) {
					return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
				}
				
				@Override
				public Object[] getChildren(Object o) {
					return ((ISensorDataContainer)o).getChildren();
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
