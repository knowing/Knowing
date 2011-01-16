package de.lmu.ifi.dbs.medmon.medic.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import de.lmu.ifi.dbs.medmon.medic.core.Activator;

public class MedmonPreferenceInitializer extends AbstractPreferenceInitializer {

	private static final String DIR_USER_HOME = System.getProperty("user.home");
	private static final String DIR_SEPERATOR = System.getProperty("file.separator");
	private static final String DIR_MEDMON = DIR_USER_HOME + DIR_SEPERATOR + ".medmon";
	
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.put(IMedicPreferences.DIR_USER_HOME_ID, System.getProperty("user.home"));
		node.put(IMedicPreferences.DIR_SEPERATOR_ID, System.getProperty("file.separator"));
		
		node.put(IMedicPreferences.DIR_MEDMON_ID, DIR_USER_HOME + DIR_SEPERATOR + ".medmon");
		node.put(IMedicPreferences.DIR_DERBY_ID, DIR_MEDMON + DIR_SEPERATOR + "db");
		node.put(IMedicPreferences.DIR_DPU_ID, DIR_MEDMON + DIR_SEPERATOR + "dpu");
		node.put(IMedicPreferences.DIR_CU_ID, DIR_MEDMON + DIR_SEPERATOR + "cluster");
		node.put(IMedicPreferences.DIR_PATIENT_ID, DIR_MEDMON + DIR_SEPERATOR + "patients");
		
	}	

}
