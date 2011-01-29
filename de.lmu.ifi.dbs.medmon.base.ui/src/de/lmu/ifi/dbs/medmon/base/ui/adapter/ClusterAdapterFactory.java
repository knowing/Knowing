package de.lmu.ifi.dbs.medmon.base.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;

public class ClusterAdapterFactory implements IAdapterFactory {

	private static final Class[] types = new Class[] { IWorkbenchAdapter.class, IWorkbenchColumnAdapter.class };
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adaptableObject instanceof ClusterUnit)
			return new ClusterUnitColumnAdapter();
		if(adaptableObject instanceof PatientClusterAdapter)
			return new PatientClusterColumnAdapter();
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return types;
	}

}
