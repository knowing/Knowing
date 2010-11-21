package de.lmu.ifi.dbs.medmon.developer.ui.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;

public class DPUContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		DataProcessingUnit dpu = (DataProcessingUnit) inputElement;
		List<DataProcessor> processors = dpu.getProcessors();
		DataProcessor[] returns = new DataProcessor[processors.size()];
		int index = 0;
		for (DataProcessor dataProcessor : processors)
			returns[index++] = dataProcessor;
		                                           
		return returns;
	}

}
