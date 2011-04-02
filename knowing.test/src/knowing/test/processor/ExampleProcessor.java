package knowing.test.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import de.lmu.ifi.dbs.knowing.core.query.Results;

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

	@Deprecated
	private Instances[] results = new Instances[0];
	
	/** This map contains the raw results, requested via {@link #result(BlockingQueue)} */
	private final Map<String, Instances> rawResults = new HashMap<String, Instances>();
	
	/** This map contains the transformed results, request via {@link #queryResults(BlockingQueue)} */
	private final Map<String, Instances> mappedResults = new HashMap<String, Instances>();
	
	private final List<String> labels = new ArrayList<String>();

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
				log.debug(" ### Clustered/Classified Instance ###");
				int i = 1;
				for (Instances res : result.getResults()) {
					log.debug("Result Nr. " + i++);
					log.trace(res);
					addResults(res);
				}
				setReady(true);
			}
			result = results.poll();
		}
		
		if(isReady()) {
			generateResults();
			fireProcessorStateChanged();
		}
			
	}
	
	@Override
	protected void queryResults(BlockingQueue<QueryTicket> resultTickets) {
		QueryTicket ticket = resultTickets.poll();
		while(ticket != null) {
			try {
				//TODO transform results to a in the header specified format
				// Instances[] headers = ticket.getHeaders();
				// transform(headers): rawResults -> mappedResults
				ticket.fireResult(results);
			} catch (InterruptedException e) {
				log.error("Failed to fire results", e);	
			}
			ticket = resultTickets.poll();
		}
	}
	
	/**
	 * @param res
	 */
	private void addResults(Instances res) {
		String key = res.relationName();
		rawResults.put(key, res);
	}

	/**
	 * 
	 */
	private void generateResults() {
		results = new Instances[rawResults.size()];
		int i = 0;
		for (Instances res : rawResults.values()) 
			results[i++] = res;
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
	public List<String> getClassLabels() {
		return labels;
	}
	
	@Override
	public void resetModel() {
		sampleQueries.clear();
		log.debug("Reset model");
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
