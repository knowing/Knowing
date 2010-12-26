package de.lmu.ifi.dbs.medmon.medic.ui.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterFile;

public class ClusterFileLabelProvider extends LabelProvider implements ITableLabelProvider{
	
	@Override
	public String getText(Object element) {
		if(element instanceof ClusterFile)
			return ((ClusterFile)element).getLabel();
		return super.getText(element);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(!(element instanceof ClusterFile))
			return "Unkown";
		ClusterFile file = (ClusterFile)element;
		switch (columnIndex) {
		case 0:
			return file.getLabel();
		case 1:
			return file.getFile();
		default:
			return "";
		}
	}

}
