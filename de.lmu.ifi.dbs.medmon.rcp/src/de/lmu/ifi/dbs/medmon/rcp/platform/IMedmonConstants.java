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
	public static final String ALGORITHM_MANAGEMENT_VIEW = "de.lmu.ifi.dbs.medmon.algorithm.Management";
	public static final String PATIENT_LIST_VIEW = "de.lmu.ifi.dbs.medmon.patient.views.PatientListView";
	public static final String SENSOR_DATA_VIEW = "de.lmu.ifi.dbs.medmon.sensor.SensorDataView";
	public static final String SENSOR_DATA_TREE_VIEW = "de.lmu.ifi.dbs.medmon.sensor.DataTreeView";
	public static final String SENSOR_MANAGEMENT_VIEW = "de.lmu.ifi.dbs.medmon.sensor.Management";
	public static final String THERAPY_MANAGEMENT_VIEW = "de.lmu.ifi.dbs.medmon.therapy.view.DiseaseManagement";
	
	
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
	public static final String IMG_ARROW_DOWN_16 = "icons/16/gtk-go-down.png";
	public static final String IMG_ARROW_UP_16 = "icons/16/gtk-go-up.png";
	public static final String IMG_REMOVE_16 = "icons/16/gtk-remove.png";
	public static final String IMG_REFRESH_16 = "icons/16/gtk-refresh.png";
	public static final String IMG_OPEN_16 = "icons/16/gtk-open.png";
	public static final String IMG_VIEW_DETAIL_16 = "icons/16/view_detailed.png";
	public static final String IMG_VIEW_TREE_16 = "icons/16/view_tree.png";
	//public static final String CHART_16 = "icons"; 
	
	//Images 24x24
	public static final String IMG_ARROW_DOWN_24 = "icons/24/gtk-go-down.png";
	public static final String IMG_ARROW_UP_24 = "icons/24/gtk-go-up.png";
	public static final String IMG_REMOVE_24 = "icons/24/gtk-remove.png";
	public static final String IMG_REFRESH_24 = "icons/24/gtk-refresh.png";
	public static final String IMG_OPEN_24 = "icons/24/gtk-open.png";
	
	//Images 48x48
	public static final String IMG_DIRECTORY_48 = "icons/48/gtk-directory.png";
	public static final String IMG_CHART_48 = "icons/48/gtk-chart.png";
	public static final String IMG_SEARCH_48 = "icons/48/gtk-find.png";
	public static final String IMG_DATA_48 = "icons/48/gtk-removable.png";
	
	//Images BIG
	public static final String IMG_ARROW_DOWN_BIG = "icons/gtk-go-down.png";
}
