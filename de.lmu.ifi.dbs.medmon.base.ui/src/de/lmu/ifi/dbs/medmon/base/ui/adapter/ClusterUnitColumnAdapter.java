package de.lmu.ifi.dbs.medmon.base.ui.adapter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;

public class ClusterUnitColumnAdapter implements IWorkbenchColumnAdapter {

	@Override
	public Object[] getChildren(Object o) {
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	@Override
	public String getLabel(Object o) {
		return ((ClusterUnit)o).getName();
	}

	@Override
	public Object getParent(Object o) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		ClusterUnit cu = (ClusterUnit)element;
		switch (columnIndex) {
		case 0: return cu.getName();
		case 1: return cu.getDescription();
		case 2: return String.valueOf(cu.getClusterlist().size());
		default: return "-";
			
		}
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

}
