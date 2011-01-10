package de.lmu.ifi.dbs.medmon.datamining.core.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

public class ProcessorAdapterFactory implements IAdapterFactory {

	private static final Class[] types = { IWorkbenchAdapter.class };


	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (!(adaptableObject instanceof IDataProcessor))
			return null;

		if (adapterType.equals(IWorkbenchAdapter.class))
			return new ProcessorWorkbenchAdapater((IDataProcessor) adaptableObject);


		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return types;
	}

}
