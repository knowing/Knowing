package de.lmu.ifi.dbs.medmon.datamining.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.DoubleCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.ClusterParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.NumericParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.StringParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;

public class Activator extends AbstractUIPlugin implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.lmu.ifi.dbs.medmon.datamining.core";
		
	// The shared instance
	private static Activator plugin;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		createConfigDir();
		//createDPUXML();
		//createClusterXML();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
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

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	private void createConfigDir() {
		File dpuDir = new File(IMedmonConstants.DIR_DPU);
		if(!dpuDir.exists())
			dpuDir.mkdirs();
		
		File cuDir = new File(IMedmonConstants.DIR_CU);
		if(!cuDir.exists())
			cuDir.mkdirs();
		
	}
	
	public void createClusterXML() {
		List<DoubleCluster> clusterlist = new ArrayList<DoubleCluster>();
		
		DoubleCluster c1 = new DoubleCluster("Test1", new double[] {2.0, 1.0, 3.0});
		DoubleCluster c2 = new DoubleCluster("Test2", new double[] {1.0, 1.0, 3.0});
		DoubleCluster c3 = new DoubleCluster("Test3", new double[] {2.0, 1.0, 4.0});
		
		clusterlist.add(c1);
		clusterlist.add(c2);
		clusterlist.add(c3);
		
		ClusterUnit clusterUnit = new ClusterUnit();
		clusterUnit.setName("Unit");
		clusterUnit.setClusterlist(clusterlist);
		
		Marshaller m = null;
		try {
			JAXBContext context = JAXBContext.newInstance(ClusterUnit.class);
			m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(clusterUnit, System.out);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public void createDPUXML() {
		System.out.println("Activator.createDPUXML()");
		List<XMLDataProcessor> processors = new ArrayList<XMLDataProcessor>();
		
		//Create Processor-Models
		XMLDataProcessor dpm1 = new XMLDataProcessor();
		dpm1.setName("Filter1");
		dpm1.setId("de.lmu.ifi.dbs.medmon.filter1");
		dpm1.setProvidedby("de.lmu.ifi.dbs.medmon");
		
		Map<String, IProcessorParameter> parameters = new HashMap<String, IProcessorParameter>();
		parameters.put("P1", new StringParameter("P1", new String[] { "1", "2" }));
		parameters.put("P2", new NumericParameter("P2"));
		parameters.put("Cluster", new ClusterParameter("home"));
		dpm1.setParameters(parameters);
		
		
		XMLDataProcessor dpm2 = new XMLDataProcessor();
		dpm2.setName("Analyzer");
		dpm2.setId("de.lmu.ifi.dbs.medmon.analyzer");
		dpm2.setProvidedby("de.lmu.ifi.dbs.medmon");
		dpm2.setParameters(parameters);
		
		processors.add(dpm1);
		processors.add(dpm2);	
		
		DataProcessingUnit dpu = new DataProcessingUnit();
		dpu.setName("Non functional");
		dpu.setProcessors(processors);
		
		Marshaller m = null;
		try {
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dpu, System.out);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		Writer w = null;
		try {
			File file = new File(IMedmonConstants.DIR_DPU + IMedmonConstants.DIR_SEPERATOR + dpu.getName() + ".xml");
			
			if(!file.exists())
				file.createNewFile();
			w = new FileWriter(file);
			m.marshal(dpu, w);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} finally {
			try {
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
	}

}
