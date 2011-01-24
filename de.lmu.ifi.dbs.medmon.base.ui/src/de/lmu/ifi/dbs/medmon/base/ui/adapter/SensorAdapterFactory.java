package de.lmu.ifi.dbs.medmon.base.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.medmon.sensor.core.util.SensorAdapter;

public class SensorAdapterFactory implements IAdapterFactory {

	private static final Class[] types = new Class[] { IWorkbenchColumnAdapter.class, IWorkbenchAdapter.class };
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(!(adaptableObject instanceof SensorAdapter))
			return null;
		
		if(adapterType.equals(IWorkbenchColumnAdapter.class)) 
			return new SensorColumnAdapter();
		else if(adapterType.equals(IWorkbenchAdapter.class)) 
			return new SensorColumnAdapter();
		
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return types;
	}

}
