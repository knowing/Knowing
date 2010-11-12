package de.lmu.ifi.dbs.medmon.patient.service;

import org.eclipse.jface.viewers.ISelection;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;


/**
 * The PatientService handles all data corresponding to
 * the current selected Patient.
 * 
 * @author muki
 * @version 0.2b
 */
public interface IPatientService {
	
	
	public static final String PATIENT = Patient.class.getName();
	public static final String SENSOR = "SENSOR";
	public static final String SENSOR_DATA = Data.class.getName();
	public static final String SENSOR_CONTAINER = "SENSOR_CONTAINER";
	public static final String ALGORITHM = IAlgorithm.class.getName();
	public static final String ANALYZED_DATA = IAnalyzedData.class.getName();
	
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

}
