package de.lmu.ifi.dbs.medmon.medic.core.util;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.medic.core.Activator;
import de.lmu.ifi.dbs.medmon.medic.core.preferences.IMedicPreferences;

public class ApplicationConfigurationUtil {
	
	public static String getPatientFolder(Patient patient) {
		String path = getPreferenceStore().getString(IMedicPreferences.DIR_PATIENT_ID);
		String sep  = getPreferenceStore().getString(IMedicPreferences.DIR_SEPERATOR_ID);
		
		StringBuffer sb = new StringBuffer();
		sb.append(path);
		sb.append(sep);
		sb.append(String.format("%011d", patient.getId()));
		sb.append("-");
		sb.append(patient.getLastname());
		sb.append(sep);
		String folder = sb.toString();
		
		//Create if not exists
		File patientFolder = new File(folder);
		if(!patientFolder.exists() || !patientFolder.isDirectory()) {
			patientFolder.delete();
			patientFolder.mkdirs();
			new File(patientFolder + sep + "data").mkdir();
			new File(patientFolder + sep + "cluster").mkdir();
		}
		return folder;
		
	}
	
	public static void createPatientFolder(Patient patient)  {
		String sep  = getPreferenceStore().getString(IMedicPreferences.DIR_SEPERATOR_ID);
		
		StringBuffer sb = new StringBuffer();
		sb.append(getPatientFolder(patient));
		File patient_dir = new File(sb.toString());
		if(patient_dir.mkdirs()) {
			if(!patient_dir.isDirectory()) {
				if(patient_dir.delete())
					patient_dir.mkdirs();
			}
		}
		sb.append(sep);
		new File(sb.toString() + "cluster").mkdir();
		new File(sb.toString() + "data").mkdir();
		
	}
	
	public static IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
}
