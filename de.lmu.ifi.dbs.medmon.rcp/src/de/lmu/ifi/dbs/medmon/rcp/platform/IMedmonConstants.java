package de.lmu.ifi.dbs.medmon.rcp.platform;

public interface IMedmonConstants {
	
	//Plugin ID
	public static final String RCP_PLUGIN = "de.lmu.ifi.dbs.medmon.rcp";

	//Perspectives
	public static final String MANAGEMENT_PERSPECTIVE = "de.lmu.ifi.dbs.medmon.patient.ManagementPerspective";
	public static final String PATIENT_PERSPECTIVE = "de.lmu.ifi.dbs.medmon.patient.PatientManagement";
	public static final String VISUALIZE_PERSPECTIVE_DEFAULT = "de.lmu.ifi.dbs.medmon.visualizer.default";
	
	
	//Views
	public static final String ALGORITHM_VIEW = "de.lmu.ifi.dbs.medmon.algorithm.views.AlgorithmView";
	public static final String ALGORITHM_MANAGEMENT = "de.lmu.ifi.dbs.medmon.algorithm.Management";
	public static final String PATIENT_LIST_VIEW = "de.lmu.ifi.dbs.medmon.patient.views.PatientListView";
	public static final String SENSOR_DATA_VIEW = "de.lmu.ifi.dbs.medmon.sensor.SensorDataView";
	public static final String SENSOR_DATA_TREE_VIEW = "de.lmu.ifi.dbs.medmon.sensor.DataTreeView";
	public static final String SENSOR_MANAGEMENT_VIEW = "de.lmu.ifi.dbs.medmon.sensor.Management";
	
	
	//Editors
	public static final String ALGORITHM_EDITOR = "de.lmu.ifi.dbs.medmon.algorithm.AlgorithmEditor";
	public static final String PATIENT_EDITOR = "de.lmu.ifi.dbs.medmon.patient.PatientEditor";
	
	//Commands
	public static final String CALL_PATIENT_EDITOR = "de.lmu.ifi.dbs.medmon.patient.OpenPatientEditor";
	public static final String CALL_IMPORT_WIZARD = "de.lmu.ifi.dbs.medmon.sensor.CallImportWizard";
	public static final String OPEN_DEFAULT_VISUALIZE_PERSPECTIVE = "de.lmu.ifi.dbs.medmon.visualizer.OpenDefaultPerspective";
	
	//Actionsets
	public static final String PATIENT_ACTIONSET = "de.lmu.ifi.dbs.medmon.patient.actionSet";
	
	//TODO Add all Images
	
	//Images 16x16
	//public static final String CHART_16 = "icons"; 
	
	//Images 48x48
	public static final String DIRECTORY_48 = "icons/48/gtk-directory.png";
	public static final String CHART_48 = "icons/48/gtk-chart.png";
	
	//Images BIG
	public static final String ARROW_DOWN_BIG = "icons/gtk-go-down.png";
}
