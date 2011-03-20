package knowing.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import knowing.test.factory.ARFFLoaderFactory;
import knowing.test.factory.CSVLoaderFactory;
import knowing.test.factory.ExampleProcessorFactory;
import knowing.test.factory.KMeansFactory;
import knowing.test.processor.SimpleKMeansProcessor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.lmu.ifi.dbs.knowing.core.graph.Edge;
import de.lmu.ifi.dbs.knowing.core.graph.GraphSupervisor;
import de.lmu.ifi.dbs.knowing.core.graph.InputNode;
import de.lmu.ifi.dbs.knowing.core.graph.ProcessorNode;
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.util.FactoryUtil;

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
		
		//Registering all factories
		FactoryUtil.registerProcesorFactory(new ExampleProcessorFactory(), null);
		FactoryUtil.registerProcesorFactory(new KMeansFactory(), null);
		FactoryUtil.registerLoaderFactory(new ARFFLoaderFactory(), null);
		FactoryUtil.registerLoaderFactory(new CSVLoaderFactory(), null);
		//Start little tests
		//kmeansTest();
		dpuTest();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
		
	/* ========== SIMPLE KMEANS TEST =========== */
	
	private void kmeansTest() {
		GraphSupervisor supervisor = new GraphSupervisor();
		InputNode inputNode = new InputNode(ARFFLoaderFactory.ID, "Input");
		ProcessorNode kmeansNode = new ProcessorNode("KMeans", KMeansFactory.ID, "KMeans");
		ProcessorNode sampleNode = new ProcessorNode("ExampleProcessor", ExampleProcessorFactory.ID, "Example");
		
		Edge e1 = new Edge("e1", "Input", "KMeans");
		Edge e2 = new Edge("e2", "Input", "Example");
		Edge e3 = new Edge("e3", "KMeans", "Example");
		
		
		supervisor.putNode(inputNode);
		supervisor.putNode(kmeansNode);
		supervisor.putNode(sampleNode);
		
		supervisor.addEdge(e1);
		supervisor.addEdge(e2);
		supervisor.addEdge(e3);
		
		supervisor.connectNodes();
		try {
			supervisor.evaluate();
		} catch (Exception e) {
			//Something bad happend during initialization
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(2000);
			supervisor.printHistory(System.err);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/* ========== DPU TEST WITH KMEANS =========== */
	
	private void dpuTest() {
		DataProcessingUnit dpu = new DataProcessingUnit();
		dpu.setName("Sample DPU");
		dpu.setDescription("A Description is here");
		dpu.setTags("Tag1, Tag2, Tag3");
		
		InputNode inputNode = new InputNode(ARFFLoaderFactory.ID, "Input");
		inputNode.setProperties(sampleProperties());
		ProcessorNode kmeansNode = new ProcessorNode("KMeans", KMeansFactory.ID, "KMeans");
		kmeansNode.setProperties(sampleProperties());
		ProcessorNode sampleNode = new ProcessorNode("ExampleProcessor", ExampleProcessorFactory.ID, "Example");
		sampleNode.setProperties(sampleProperties());
		
		Edge e1 = new Edge("e1", "Input", "KMeans");
		Edge e2 = new Edge("e2", "Input", "Example");
		Edge e3 = new Edge("e3", "KMeans", "Example");
		
		dpu.add(inputNode);
		dpu.add(kmeansNode);
		dpu.add(sampleNode);
		
		dpu.addEdge(e1);
		dpu.addEdge(e2);
		dpu.addEdge(e3);
		
		/* == Persist DPU == */
		String path = System.getProperty("user.home") + System.getProperty("file.separator") + "dpu.xml";
		File dpufile = new File(path);
		JAXBContext context = null;
		try {
			context = JAXBContext.newInstance(DataProcessingUnit.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dpu, dpufile);
			//m.marshal(dpu, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		/* == Load DPU == */
		try {
			Unmarshaller um = context.createUnmarshaller();
			dpu = (DataProcessingUnit) um.unmarshal(dpufile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		GraphSupervisor supervisor = new GraphSupervisor(dpu);
		supervisor.connectNodes();
		try {
			supervisor.evaluate();
		} catch (Exception e) {
			//Something bad happend during initialization
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(2000);
			supervisor.printHistory(System.err);
			
			System.out.println("Trying to persists kmeans model");
			supervisor.persistNode("KMeans", sampleOutput());
			System.out.println("Persisted!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private Properties sampleProperties() {
		Properties properties = new Properties();
		properties.setProperty(SimpleKMeansProcessor.PROP_MAX_ITERATIONS, "20");
		properties.setProperty(SimpleKMeansProcessor.PROP_NUM_CLUSTERS, "3");
		return properties;
	}
	
	private OutputStream sampleOutput() throws FileNotFoundException {
		String pathname = System.getProperty("user.home") + "/" + "kmeans.model";
		return new FileOutputStream(pathname);
	}

}
