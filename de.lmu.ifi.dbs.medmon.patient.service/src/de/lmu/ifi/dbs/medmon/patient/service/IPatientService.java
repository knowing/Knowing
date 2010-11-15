package de.lmu.ifi.dbs.medmon.patient.service;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelection;


/**
 * The PatientService handles all data corresponding to
 * the current selected Patient.
 * 
 * @author muki
 * @version 0.2b
 */
public interface IPatientService {
	
	
	public static final String PATIENT = "de.lmu.ifi.dbs.medmon.database.model.patient";
	public static final String SENSOR = "de.lmu.ifi.dbs.medmon.sensor.core.sensor.isensor";
	public static final String SENSOR_DATA = "de.lmu.ifi.dbs.medmon.database.model.data";
	public static final String SENSOR_CONTAINER = "de.lmu.ifi.dbs.medmon.sensor.core.container";
	public static final String ALGORITHM = "de.lmu.ifi.dbs.medmon.datamining.processing.algorithm";
	public static final String ANALYZED_DATA = "de.lmu.ifi.dbs.medmon.datamining.processing.ianalyzed";
	
	/**
	 * Checks if the selection is supported and add it.
	 * @param selection
	 * @return true if the selection was successfully set
	 */
	public boolean setSelection(ISelection selection);
	
	public boolean setSelection(Object object, String clazz);
	
	/**
	 * 
	 * @param clazz 
	 * <li> PATIENT (Patient.class.getName())
	 * <li> SENSOR_DATA (SensorData.class.getName())
	 * <li> ALGORITHM (ISensorDataAlgorithm.class.getName())
	 * @return the Object if selected
	 */
	public Object getSelection(String clazz);
	
	public void addPropertyChangeListener(String property, PropertyChangeListener listener);
	
	public void removePropertyChangeListener(PropertyChangeListener listener);

}
