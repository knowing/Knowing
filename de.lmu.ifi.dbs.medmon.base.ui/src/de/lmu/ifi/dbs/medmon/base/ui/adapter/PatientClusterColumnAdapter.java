package de.lmu.ifi.dbs.medmon.base.ui.adapter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;


public class PatientClusterColumnAdapter implements IWorkbenchColumnAdapter {

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
		return ((PatientClusterAdapter)o).getName();
	}

	@Override
	public Object getParent(Object o) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		PatientClusterAdapter adapter = (PatientClusterAdapter) element;
		switch (columnIndex) {
		case 0: return adapter.getName();
		case 1: return adapter.getDescription();
		case 2: return String.valueOf(adapter.isDefault());
		default: return "";
		}
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if(columnIndex == 2) {
			PatientClusterAdapter adapter = (PatientClusterAdapter) element;
			return null;
		}
			
		return null;
	}

}