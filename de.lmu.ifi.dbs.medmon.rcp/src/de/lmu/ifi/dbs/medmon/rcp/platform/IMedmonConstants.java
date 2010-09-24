package de.lmu.ifi.dbs.medmon.rcp.platform;

public interface IMedmonConstants {

	//Perspectives
	public static final String PATIENT_PERSPECTIVE = "de.lmu.ifi.dbs.medmon.patient.PatientManagement";
	public static final String VISUALIZE_PERSPECTIVE_DEFAULT = "de.lmu.ifi.dbs.medmon.visualizer.default";
	
	
	//Views
	public static final String ALGORITHM_VIEW = "de.lmu.ifi.dbs.medmon.algorithm.views.AlgorithmView";
	public static final String PATIENT_LIST_VIEW = "de.lmu.ifi.dbs.medmon.patient.views.PatientListView";
	public static final String SENSOR_DATA_VIEW = "de.lmu.ifi.dbs.medmon.sensor.SensorDataView";
	public static final String SENSOR_DATA_TREE_VIEW = "de.lmu.ifi.dbs.medmon.sensor.DataTreeView";
	
	
	//Editors
	public static final String ALGORITHM_EDITOR = "de.lmu.ifi.dbs.medmon.algorithm.AlgorithmEditor";
	public static final String PATIENT_EDITOR = "de.lmu.ifi.dbs.medmon.patient.PatientEditor";
	
	//Commands
	public static final String CALL_PATIENT_EDITOR = "de.lmu.ifi.dbs.medmon.patient.OpenPatientEditor";
	public static final String CALL_IMPORT_WIZARD = "de.lmu.ifi.dbs.medmon.sensor.CallImportWizard";
	public static final String OPEN_DEFAULT_VISUALIZE_PERSPECTIVE = "de.lmu.ifi.dbs.medmon.visualizer.OpenDefaultPerspective";
	
	//Actionsets
	public static final String PATIENT_ACTIONSET = "de.lmu.ifi.dbs.medmon.patient.actionSet";
}
