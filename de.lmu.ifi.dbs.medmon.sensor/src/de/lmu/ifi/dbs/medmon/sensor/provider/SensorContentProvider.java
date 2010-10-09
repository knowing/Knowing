package de.lmu.ifi.dbs.medmon.sensor.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.sensor.sensors.Sensor3D;

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
		return new Sensor3D[] {new Sensor3D()};
	}	
}
