package de.lmu.ifi.dbs.medmon.sensor.ui.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;
import de.lmu.ifi.dbs.medmon.sensor.ui.viewer.SensorTableViewer;

public class SensorLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		ISensor sensor = (ISensor)element;
		switch(columnIndex) {
		case SensorTableViewer.COL_NAME: return sensor.getName();
		case SensorTableViewer.COL_VERSION: return sensor.getVersion();
		case SensorTableViewer.COL_TYPE: return type(sensor.getType());
		default: return "--";
		}
	}

	private String type(int type) {
		switch(type) {
		case ISensor.MASTER: return "Master";
		case ISensor.SLAVE: return "Slave";
		default: return "unbekannt";
		}	
	}

}
