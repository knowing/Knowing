package de.lmu.ifi.dbs.medmon.developer.ui.adapter;

import org.eclipse.core.runtime.IAdaptable;

public class DataProcessorElement implements IAdaptable {

	@Override
	public Object getAdapter(Class adapter) {
		System.out.println("Class adapter: " + adapter);
		return null;
	}

}
