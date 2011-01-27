package de.lmu.ifi.dbs.medmon.base.ui.adapter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import de.lmu.ifi.dbs.medmon.base.ui.Activator;
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
		return ((SensorAdapter) o).getName();
	}

	@Override
	public Object getParent(Object o) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		SensorAdapter adapter = (SensorAdapter) element;
		switch (columnIndex) {
		case 0:
			return adapter.getName();
		case 1:
			return adapter.getVersion();
		case 2:
			return adapter.getType();
		case 3:
			return adapter.getDefaultPath();
		case 4:
			return status(adapter.isAvailable());
		default:
			return "-";
		}
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 4) {
			SensorAdapter adapter = (SensorAdapter) element;
			if (adapter.isAvailable())
				return Activator.getImageDescriptor("icons/sensor_16.png").createImage();
			return Activator.getImageDescriptor("icons/sensor_disabled_16.png").createImage();
		}

		return null;
	}
	
	private String status(boolean available) {
		if(available)
			return "ready";
		return "";
	}

}
