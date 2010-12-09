package de.lmu.ifi.dbs.medmon.developer.ui.csv;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.datamining.core.csv.CSVField;

public class CSVFieldContentProvider implements IStructuredContentProvider {

	
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<CSVField> fields = (List<CSVField>) inputElement;
		return fields.toArray(new CSVField[fields.size()]);
	}

}
