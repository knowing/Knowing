package knowing.test;

import knowing.test.factory.ARFFLoaderFactory;
import knowing.test.factory.CSVLoaderFactory;
import knowing.test.factory.ExampleProcessorFactory;
import knowing.test.factory.KMeansFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.lmu.ifi.dbs.knowing.core.graph.Edge;
import de.lmu.ifi.dbs.knowing.core.graph.GraphSupervisor;
import de.lmu.ifi.dbs.knowing.core.graph.InputNode;
import de.lmu.ifi.dbs.knowing.core.graph.ProcessorNode;
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
		//Start little test
		kmeansTest();
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

}
