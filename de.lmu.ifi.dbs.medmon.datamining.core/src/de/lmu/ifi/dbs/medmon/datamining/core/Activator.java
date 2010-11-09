package de.lmu.ifi.dbs.medmon.datamining.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.DoubleCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		createConfigDir();
		//createTestXML();
		createClusterXML();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	private void createConfigDir() {
		File dir = new File(IMedmonConstants.DIR_DPU);
		if(!dir.exists())
			dir.mkdirs();
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
	
	public void createTestXML() {
		List<DataProcessor> processors = new ArrayList<DataProcessor>();
		
		//Create Processor-Models
		DataProcessor dpm1 = new DataProcessor();
		dpm1.setName("Filter1");
		dpm1.setId("de.lmu.ifi.dbs.medmon.filter1");
		dpm1.setProvidedby("de.lmu.ifi.dbs.medmon");
		
		DataProcessor dpm2 = new DataProcessor();
		dpm2.setName("Analyzer");
		dpm2.setId("de.lmu.ifi.dbs.medmon.analyzer");
		dpm2.setProvidedby("de.lmu.ifi.dbs.medmon");
		
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
