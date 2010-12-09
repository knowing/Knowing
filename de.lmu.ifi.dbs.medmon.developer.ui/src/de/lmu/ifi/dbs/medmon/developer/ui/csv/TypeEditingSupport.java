package de.lmu.ifi.dbs.medmon.developer.ui.csv;

import java.util.Date;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.lmu.ifi.dbs.medmon.datamining.core.csv.CSVField;

public class TypeEditingSupport extends EditingSupport {

	private final TableViewer viewer;

	public TypeEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		String[] classes = new String[] { Double.class.getName(), Date.class.getName()};
		return new ComboBoxCellEditor(viewer.getTable(), classes);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		CSVField field = (CSVField)element;
		if(field.getType().equals(Double.class))
			return 0;
		if(field.getType().equals(Date.class))
			return 1;
		return 0;
	}

	@Override
	protected void setValue(Object element, Object value) {
		Integer index = (Integer) value;
		CSVField field = (CSVField)element;
		switch(index) {
		case 0: field.setType(Double.class);
				break;
		case 1: field.setType(Date.class);
		}
		viewer.refresh();
	}

}
