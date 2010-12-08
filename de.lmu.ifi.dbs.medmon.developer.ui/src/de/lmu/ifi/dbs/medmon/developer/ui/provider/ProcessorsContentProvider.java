package de.lmu.ifi.dbs.medmon.developer.ui.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

public class ProcessorsContentProvider implements IStructuredContentProvider {

	private List<DataProcessor> processors;
	private Viewer viewer;
	
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;	
	}

	@Override
	public Object[] getElements(Object inputElement) {
		//Refresh list by giving a new list
		if(inputElement != processors && inputElement != null && inputElement instanceof List<?>)
			processors = (List<DataProcessor>) inputElement;
		if(processors == null)
			processors = new LinkedList<DataProcessor>();
		
		if(inputElement instanceof DataProcessor)
			processors.add((DataProcessor) inputElement);
		if(inputElement instanceof IDataProcessor)
			processors.add(new DataProcessor((IDataProcessor)inputElement));
		
		if(inputElement instanceof DataProcessor[]) {
			DataProcessor[] processor = (DataProcessor[]) inputElement;
			for (DataProcessor each : processor)
				processors.add(each);
		}
		if(inputElement instanceof IDataProcessor[]) {
			IDataProcessor[] processor = (IDataProcessor[]) inputElement;
			for (IDataProcessor each : processor)
				processors.add(new DataProcessor(each));
		}
		
		DataProcessor[] returns = new DataProcessor[processors.size()];
		return processors.toArray(returns);
	}
	

}
