package de.lmu.ifi.dbs.medmon.base.ui.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterFile;
import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterTableItem;

public class ClusterTableItemLabelProvider extends LabelProvider implements ITableLabelProvider{
	
	@Override
	public String getText(Object element) {
		if(element instanceof ClusterTableItem<?>)
			return ((ClusterTableItem<?>)element).getLabel();
		return super.getText(element);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(!(element instanceof ClusterTableItem<?>))
			return "Unkown";
		ClusterTableItem<?> file = (ClusterTableItem<?>)element;
		switch (columnIndex) {
		case 0:
			return file.getLabel();
		case 1:
			return file.getSource().toString();
		default:
			return "";
		}
	}

}
