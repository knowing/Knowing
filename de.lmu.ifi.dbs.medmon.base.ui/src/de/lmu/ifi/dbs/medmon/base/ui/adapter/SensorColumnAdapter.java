package de.lmu.ifi.dbs.medmon.base.ui.adapter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import de.lmu.ifi.dbs.medmon.sensor.core.util.SensorAdapter;

public class SensorColumnAdapter implements IWorkbenchColumnAdapter {
	
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
		return((SensorAdapter)o).getName();
	}

	@Override
	public Object getParent(Object o) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		SensorAdapter adapter = (SensorAdapter) element;
		switch (columnIndex) {
		case 0:	return adapter.getName();
		case 1: return adapter.getVersion();
		case 2: return adapter.getType();
		case 3: return adapter.getDefaultPath();
		case 4: return String.valueOf(adapter.isAvailable());
		default: return adapter.getName();
		}
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		
		return null;
	}

}
