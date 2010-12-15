package de.lmu.ifi.dbs.medmon.medic.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.medic.core.extensions.IDisease;
import de.lmu.ifi.dbs.medmon.medic.core.unit.MedicProcessingUnit;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.lmu.ifi.dbs.medmon.therapy.core"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	//Tracks disease service
	private static ServiceTracker diseaseTracker;
	//Tracks patient service
	private static ServiceTracker patientTracker;
	
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
		
		diseaseTracker = new ServiceTracker(context, IDisease.class.getName(), null);
		diseaseTracker.open();
		
		patientTracker = new ServiceTracker(context, IPatientService.class.getName(), null);
		patientTracker.open();
		
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		diseaseTracker.close();
		patientTracker.close();
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

	public static IDisease[] getIDiseaseServices() {
		return (IDisease[]) diseaseTracker.getServices();
	}
	
	public static IPatientService getPatientService() {
		return (IPatientService) patientTracker.getService();
	}
}
