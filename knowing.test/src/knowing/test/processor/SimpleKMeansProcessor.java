package knowing.test.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import weka.clusterers.SimpleKMeans;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;
import de.lmu.ifi.dbs.knowing.core.processing.Processor;
import de.lmu.ifi.dbs.knowing.core.query.Queries;
import de.lmu.ifi.dbs.knowing.core.query.QueryResult;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

public class SimpleKMeansProcessor extends Processor {

	public static final String PROP_MAX_ITERATIONS = "max-iterations";
	public static final String PROP_NUM_CLUSTERS = "num-clusters";

	private SimpleKMeans kmeans;
	private volatile boolean ready;

	public SimpleKMeansProcessor() {
		kmeans = new SimpleKMeans();
		getProperties().clear();
		setProperty(PROP_MAX_ITERATIONS, "20");
		setProperty(PROP_NUM_CLUSTERS, "5");
	}

	public SimpleKMeansProcessor(Properties properties) {
		this();
		try {
			if (properties != null) {
				Integer iter = Integer.valueOf(properties.getProperty(PROP_MAX_ITERATIONS, "20"));
				Integer clus = Integer.valueOf(properties.getProperty(PROP_NUM_CLUSTERS, "5"));

				kmeans.setMaxIterations(iter);
				setProperty(PROP_MAX_ITERATIONS, iter.toString());
				log.debug("Set KMeans property " + PROP_MAX_ITERATIONS + " to " + iter);
				
				kmeans.setNumClusters(clus);
				setProperty(PROP_NUM_CLUSTERS, clus.toString());
				log.debug("Set KMeans property " + PROP_NUM_CLUSTERS + " to " + clus);

			} else {
				Integer iter = Integer.valueOf(getProperties().getProperty(PROP_MAX_ITERATIONS));
				Integer clus = Integer.valueOf(getProperties().getProperty(PROP_NUM_CLUSTERS));
				kmeans.setMaxIterations(iter);
				setProperty(PROP_MAX_ITERATIONS, iter.toString());
				kmeans.setNumClusters(clus);
				setProperty(PROP_NUM_CLUSTERS, clus.toString());
			}
		} catch (Exception e) {
			log.error("Not able to set properties for " + kmeans, e);
		}

	}

	@Override
	public synchronized void buildModel(ILoader loader) {
		log.debug(" ### KMeansProcessor: Build Model from Loader ###");
		try {
			fireQuery("model.loader", loader, false);
		} catch (IOException e) {
			log.error("IO Error while building model from " + loader, e);
		}
	}

	@Override
	public synchronized void buildModel(IProcessor processor) {

	}

	@Override
	public void resetModel() {
		log.debug("Reset model for " + kmeans);
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
		while (ticket != null) {
			log.trace(this + " answering ticket [" + ticket + "]");
			Instance input = ticket.getQuery();
			int clusterInstance;
			try {
				clusterInstance = kmeans.clusterInstance(input);
				Instance instance = kmeans.getClusterCentroids().get(clusterInstance);
				double[] probs = kmeans.distributionForInstance(input);
				// Creating results
				Instances[] results = new Instances[2];
				Instances instanceSet = new Instances(instance.dataset());
				instanceSet.add(instance);
				results[0] = instanceSet;
				results[1] = Queries.arrayNumericQuery(probs);
				// fire results
				ticket.fireResult(results);
				// get next ticket
				ticket = tickets.poll();

			} catch (Exception e) {
				log.error("Error while querying kmeans", e);
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
					log.info(" ### Cluster build finished ### ");
					log.debug(kmeans.getClusterCentroids());
					log.debug(" ### ====================== ###");
					ready = true;
					fireProcessorStateChanged();
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
		ArffSaver saver = new ArffSaver();
		log.info("Persisting KMeans model");
		log.debug(kmeans.getClusterCentroids());
		if(kmeans.getClusterCentroids() == null)
			return;
		try {
			saver.setDestination(out);
			saver.setInstances(kmeans.getClusterCentroids());
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
