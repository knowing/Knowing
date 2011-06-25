package de.lmu.ifi.dbs.knowing.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.knowing.core.graph.Node;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 09.05.2011
 *
 */
public class NodeAdapterFactory implements IAdapterFactory {

	private static final Class[] types = new Class[] { IWorkbenchAdapter.class , IWorkbenchColumnAdapter.class};
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(!(adaptableObject instanceof Node)) 
			return null;
			
		return new NodeAdapter();
	}

	@Override
	public Class[] getAdapterList() {
		return types;
	}

}
