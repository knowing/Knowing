package knowing.test.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import weka.core.Instance;
import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;
import de.lmu.ifi.dbs.knowing.core.processing.IResultProcessor;
import de.lmu.ifi.dbs.knowing.core.processing.Processor;
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
 * @version 1.0
 *
 */
public class ExampleProcessor extends Processor implements IResultProcessor {

	private static final Instances[] supported = new Instances[] { Queries.arrayNumericQuery(),
			Queries.singleNumericQuery() };

	private BlockingQueue<Instance> sampleQueries = new ArrayBlockingQueue<Instance>(10, true);
	private volatile boolean ready;

	private Instances resultSet;

	@Override
	public synchronized void buildModel(ILoader loader) {
		try {
			fireQuery("model.loader", loader, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void buildModel(IProcessor processor) {
		try {
			Instance input = sampleQueries.poll(10, TimeUnit.SECONDS);
			while (input != null) {
				System.out.println(">>>> Input: " + input);
				fireQuery("sample.query", input, processor);
				input = sampleQueries.poll();
				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
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
				System.out.println(" ### Clustered Instance ###");
				int index = 0;
				for (Instances res : result.getResults()) {
					System.out.println("Result Nr. " + index++);
					System.out.println(res);
					this.resultSet = res;
				}
				ready = true;
				fireProcessorStateChanged();
				System.out.println(" ### ================== ###");
			}
			result = results.poll();
		}
	}

	private void buildSampleQueries(QueryResult result) {
		Instances[] instances = result.getResults();
		Instances samples = instances[0];
		for (int i = 0; i < 5; i++) {
			int index = (int) (Math.random() * 10) % samples.numInstances();
			try {
				sampleQueries.put(samples.get(index));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void query(BlockingQueue<QueryTicket> tickets) {
		tickets.clear(); //Throw them away, we don't answer queries
	}

	@Override
	public Instances getResult() {
		return resultSet;
	}
	
	@Override
	public void resetModel() {
		sampleQueries.clear();
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
