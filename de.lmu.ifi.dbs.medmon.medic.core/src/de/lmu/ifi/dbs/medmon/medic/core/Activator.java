package de.lmu.ifi.dbs.medmon.medic.core;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.lmu.ifi.dbs.medmon.medic.core.preferences.IMedicPreferences;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.lmu.ifi.dbs.medmon.medic.core"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
		
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);			
		plugin = this;
		
		createApplicationFolders();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;

		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	

	private void createApplicationFolders() {
		IPreferenceStore store = plugin.getPreferenceStore();
		
		String root_dir = store.getString(IMedicPreferences.DIR_MEDMON_ID);
		String dpu_dir = store.getString(IMedicPreferences.DIR_DPU_ID);
		String patient_dir = store.getString(IMedicPreferences.DIR_PATIENT_ID);
		String cluster_dir = store.getString(IMedicPreferences.DIR_CU_ID);
		
		File root = new File(root_dir);
		if(!root.exists()) {
			root.mkdirs();
			new File(dpu_dir).mkdir();
			new File(patient_dir).mkdir();
			new File(cluster_dir).mkdir();		
		} else {
			File dpu = new File(dpu_dir);
			if(!dpu.exists())
				dpu.mkdir();
			File patient = new File(patient_dir);
			if(!patient.exists())
				patient.mkdir();
			File cluster = new File(cluster_dir);
			if(!cluster.exists())
				cluster.mkdir();
		}
		
	}	

}
