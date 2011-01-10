package de.lmu.ifi.dbs.medmon.developer.ui.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

public class ProcessorsContentProvider implements IStructuredContentProvider {

	private List<XMLDataProcessor> processors;
	
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		//Refresh list by giving a new list
		if(inputElement != processors && inputElement != null && inputElement instanceof List<?>)
			processors = (List<XMLDataProcessor>) inputElement;
		if(processors == null)
			processors = new LinkedList<XMLDataProcessor>();
		
		if(inputElement instanceof XMLDataProcessor)
			processors.add((XMLDataProcessor) inputElement);
		if(inputElement instanceof IDataProcessor)
			processors.add(new XMLDataProcessor((IDataProcessor)inputElement));
		
		if(inputElement instanceof XMLDataProcessor[]) {
			XMLDataProcessor[] processor = (XMLDataProcessor[]) inputElement;
			for (XMLDataProcessor each : processor)
				processors.add(each);
		}
		if(inputElement instanceof IDataProcessor[]) {
			IDataProcessor[] processor = (IDataProcessor[]) inputElement;
			for (IDataProcessor each : processor)
				processors.add(new XMLDataProcessor(each));
		}
		
		XMLDataProcessor[] returns = new XMLDataProcessor[processors.size()];
		return processors.toArray(returns);
	}
	

}
