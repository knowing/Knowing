package de.lmu.ifi.dbs.medmon.developer.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.graph.XDataProcessingUnit;
import de.lmu.ifi.dbs.medmon.developer.ui.graph.Edges;
import de.lmu.ifi.dbs.medmon.developer.ui.graph.Nodes;

public class XDPUAdapterFactory implements IAdapterFactory {

	private static final Class[] clazz = new Class[] { IWorkbenchAdapter.class };
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		//XDataProcessingUnit dpu = (XDataProcessingUnit) adaptableObject;
		if(adapterType.equals(IWorkbenchAdapter.class)) {
			return new IWorkbenchAdapter() {
				
				@Override
				public Object getParent(Object o) {
					return null;
				}
				
				@Override
				public String getLabel(Object o) {
					return ((XDataProcessingUnit)o).getName();
				}
				
				@Override
				public ImageDescriptor getImageDescriptor(Object object) {
					return null;
				}
				
				@Override
				public Object[] getChildren(Object o) {
					XDataProcessingUnit dpu = (XDataProcessingUnit) o;
					Object[] returns = new Object[2];
					returns[0] = new Nodes(dpu.getNodes().values());
					returns[1] = new Edges(dpu.getEdges());
					return returns;
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
