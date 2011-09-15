package de.lmu.ifi.dbs.knowing.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.knowing.core.model.IEdge;

public class EdgeAdapterFactory implements IAdapterFactory {

	private static final Class[] types = new Class[] { IWorkbenchAdapter.class , IWorkbenchColumnAdapter.class};
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(!(adaptableObject instanceof IEdge))
			return null;
		return new EdgeAdapter();
	}

	@Override
	public Class[] getAdapterList() {
		return types;
	}

}
