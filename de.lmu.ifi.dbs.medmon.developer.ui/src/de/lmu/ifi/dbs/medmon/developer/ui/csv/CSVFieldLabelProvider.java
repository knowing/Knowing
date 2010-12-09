package de.lmu.ifi.dbs.medmon.developer.ui.csv;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.lmu.ifi.dbs.medmon.datamining.core.csv.CSVField;

public class CSVFieldLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		CSVField field = (CSVField) element;
		switch(columnIndex) {
		case 0: return String.valueOf(field.getPosition());
		case 1: return field.getType().getName();
		}
		return getText(element);
	}

}
