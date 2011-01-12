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

import de.lmu.ifi.dbs.medmon.datamining.core.parameter.XMLParameterWrapper;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
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
		//testMPU();
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
	
	private void testMPU() {
		DataProcessingUnit dpu = new DataProcessingUnit();
		List<XMLParameterWrapper> parameters = new ArrayList<XMLParameterWrapper>();
		parameters.add(new XMLParameterWrapper("key1", "value1", "double"));
		parameters.add(new XMLParameterWrapper("key2", "value2", "double"));
		XMLDataProcessor p1 = new XMLDataProcessor("name1", "id1", "provider", parameters.toArray(new XMLParameterWrapper[parameters.size()]));
		XMLDataProcessor p2 = new XMLDataProcessor("name1", "id1", "provider", parameters.toArray(new XMLParameterWrapper[parameters.size()]));
		dpu.add(p1);
		dpu.add(p2);
		
		MedicProcessingUnit mpu = new MedicProcessingUnit();
		mpu.setDpus(Collections.singletonList(dpu));
		mpu.setName("MPU Name");
		mpu.setDescription("The description");
		
		try {
			JAXBContext context = JAXBContext.newInstance(MedicProcessingUnit.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(mpu, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
	}
}
