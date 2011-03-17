package knowing.test.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import weka.clusterers.SimpleKMeans;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;
import de.lmu.ifi.dbs.knowing.core.processing.Processor;
import de.lmu.ifi.dbs.knowing.core.query.Queries;
import de.lmu.ifi.dbs.knowing.core.query.QueryResult;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

public class SimpleKMeansProcessor extends Processor {

	private SimpleKMeans kmeans;
	private volatile boolean ready;

	public SimpleKMeansProcessor() {
		kmeans = new SimpleKMeans();
	}

	@Override
	public synchronized void buildModel(ILoader loader) {
		System.out.println(" ### KMeansProcessor: Build Model from Loader ###");
		try {
			fireQuery("model.loader", loader, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void buildModel(IProcessor processor) {

	}

	@Override
	public void resetModel() {
		kmeans = new SimpleKMeans();
		ready = false;
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	protected void query(BlockingQueue<QueryTicket> tickets) {
		QueryTicket ticket = tickets.poll();
		while(ticket != null) {
			Instance input = ticket.getQuery();
			int clusterInstance;
			try {
				clusterInstance = kmeans.clusterInstance(input);
				Instance instance = kmeans.getClusterCentroids().get(clusterInstance);
				double[] probs = kmeans.distributionForInstance(input);				
				//Creating results
				Instances[]	results = new Instances[2];
				Instances instanceSet = new Instances(instance.dataset());
				instanceSet.add(instance);
				results[0] = instanceSet;
				results[1] = Queries.arrayNumericQuery(probs);
				//fire results
				ticket.fireResult(results);
				//get next ticket
				ticket = tickets.poll();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}
	
	@Override
	protected void result(BlockingQueue<QueryResult> results) {
		QueryResult result = results.poll();
		while (result != null) {
			QueryTicket ticket = result.getTicket();
			Instances[] queryResults = result.getResults();
			if (ticket.getName().equals("model.loader")) {
				try {
					kmeans.buildClusterer(queryResults[0]);
					System.out.println(" ### Cluster build finished ### ");
					System.out.println(kmeans.getClusterCentroids());
					System.out.println(" ### ====================== ###");
					ready = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result = results.poll();
		}
	}

	@Override
	public String[] validate() {
		return null;
	}

	@Override
	public void persistModel(OutputStream out) {

	}

	@Override
	public void loadModel(InputStream in) {

	}

	@Override
	public Instances[] supportedQueries() {
		// Supports only queries == buildModel
		return null;
	}

	@Override
	public Capabilities getCapabilities() {
		return kmeans.getCapabilities();
	}
}
