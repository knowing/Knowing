package de.lmu.ifi.dbs.medmon.medic.ui.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;


/**
 * Doesn't use input
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 */
public class SensorContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return FrameworkUtil.<ISensor>evaluateExtensions(ISensor.SENSOR_ID);
	}	
}
