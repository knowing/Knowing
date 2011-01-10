package de.lmu.ifi.dbs.medmon.datamining.core.adapter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

public class ProcessorWorkbenchAdapater implements IWorkbenchAdapter {

	private final IDataProcessor processor;
	
	public ProcessorWorkbenchAdapater(IDataProcessor processor) {
		this.processor = processor;
	}
	
	public ProcessorWorkbenchAdapater(XMLDataProcessor processor) {
		this(processor.loadProcessor());
	}
	
	@Override
	public Object getParent(Object o) {
		return null;
	}
	
	@Override
	public String getLabel(Object o) {	
		return processor.getName();
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		//TODO Filter / Algorithm / ClusterAlgorithm Images
		return null;
	}
	
	@Override
	public Object[] getChildren(Object o) {
		//TODO return ParameterList
		return null;
	}

}
