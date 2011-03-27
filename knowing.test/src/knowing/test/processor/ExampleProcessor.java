package knowing.test.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import weka.core.Instance;
import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;
import de.lmu.ifi.dbs.knowing.core.processing.ResultProcessor;
import de.lmu.ifi.dbs.knowing.core.query.Queries;
import de.lmu.ifi.dbs.knowing.core.query.QueryResult;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

/**
 * <p>Example processor. Builds a sample queries from a ILoader.<br>
 * With these queries it queries an IProcessor for "building" an<br>
 * output on the console</p>
 * 
 * <p>Actually it is designed for usage with clusterers</p>
 * 
 * @author Nepomuk Seiler
 * @version 1.2
 *
 */
public class ExampleProcessor extends ResultProcessor {

	private static final Instances[] supported = new Instances[] { Queries.arrayNumericQuery(),
			Queries.singleNumericQuery() };

	private BlockingQueue<Instance> sampleQueries = new ArrayBlockingQueue<Instance>(10, true);
	private volatile boolean ready;

	private Instances[] results = new Instances[0];
	private Map<String, Instances> resultsMap = new HashMap<String, Instances>();

	@Override
	public synchronized void buildModel(ILoader loader) {
		try {
			fireQuery("model.loader", loader, false);
		} catch (IOException e) {
			log.error("IOError while building model from " + loader, e);
		}
	}

	@Override
	public synchronized void buildModel(IProcessor processor) {
		try {
			Instance input = sampleQueries.poll(10, TimeUnit.SECONDS);
			while (input != null) {
				log.debug(">>>> Input: " + input);
				fireQuery("sample.query", input, processor);
				input = sampleQueries.poll();
				
			}
		} catch (InterruptedException e) {
			log.error("InterruptedError while building model from " + processor, e);
		}
	}

	@Override
	protected void result(BlockingQueue<QueryResult> results) {
		QueryResult result = results.poll();
		while (result != null) {
			QueryTicket ticket = result.getTicket();
			// Build sample Queries from the loader
			if (ticket.getName().equals("model.loader")) {
				buildSampleQueries(result);
			} else {
				// Print the results from the queryed IProcessor
				log.debug(" ### Clustered Instance ###");
				int i = 1;
				for (Instances res : result.getResults()) {
					log.debug("Result Nr. " + i++);
					//System.out.println(res);
					addResults(res);
				}
				ready = true;
				log.debug(" ### ================== ###");
			}
			result = results.poll();
		}
		if(ready) 
			generateResults();
		
			
	}
	
	@Override
	protected void query(BlockingQueue<QueryTicket> tickets) {
		tickets.clear(); //Throw them away, we don't answer queries
	}
	
	@Override
	protected void queryResults(BlockingQueue<QueryTicket> resultTickets) {
		QueryTicket ticket = resultTickets.poll();
		while(ticket != null) {
			try {
				ticket.fireResult(results);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ticket = resultTickets.poll();
		}
	}
	
	/**
	 * @param res
	 */
	private void addResults(Instances res) {
		String key = res.relationName();
		resultsMap.put(key, res);
	}

	/**
	 * 
	 */
	private void generateResults() {
		results = new Instances[resultsMap.size()];
		int i = 0;
		for (Instances res : resultsMap.values()) 
			results[i++] = res;
		fireProcessorStateChanged();
	}

	private void buildSampleQueries(QueryResult result) {
		Instances[] instances = result.getResults();
		Instances samples = instances[0];
		for (int i = 0; i < 5; i++) {
			int index = (int) (Math.random() * 10) % samples.numInstances();
			try {
				sampleQueries.put(samples.get(index));
			} catch (InterruptedException e) {
				log.error("InterruptedError while building sampleQueries", e);
			}
		}
	}
	
	@Override
	public void resetModel() {
		sampleQueries.clear();
		log.debug("Reset model");
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public String[] validate() {
		return new String[0];
	}
	
	
	@Override
	public Instances[] supportedQueries() {
		return supported;
	}

	@Override
	public void persistModel(OutputStream out) {

	}

	@Override
	public void loadModel(InputStream in) {

	}

}
