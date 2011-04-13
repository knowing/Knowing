package de.lmu.ifi.dbs.knowing.core.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.graph.IProcessorListener;
import de.lmu.ifi.dbs.knowing.core.query.QueryResult;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;
import de.lmu.ifi.dbs.knowing.core.query.Results;

/**
 * <p>Implements some query methods and synchronizes them, so a robust and reliable<br>
 * service will be generated.</p>
 * 
 * <p>To use the provided Logger put or add these dependencies to your Manifest.MF in your bundle:<br>
 * <code>
 * Import-Package: org.apache.log4j; version="[1.2,1.3)"; provider=paxlogging,
 * org.apache.commons.logging; version="[1.0,1.1)"; provider=paxlogging
 * </code>
 * </p>
 * 
 * @author Nepomuk Seiler
 * @version 0.6.1
 */
public abstract class Processor implements IProcessor {
	
	public static final String QUERY_LOADER_DATASET = "loader.dataset";
	public static final String QUERY_LOADER_STRUCTURE = "loader.structure";

	private final Properties properties = new Properties();

	private final BlockingQueue<QueryResult> results = new LinkedBlockingQueue<QueryResult>();
	private final BlockingQueue<QueryTicket> tickets = new ArrayBlockingQueue<QueryTicket>(1000, true);
	
	private List<String> labels = new ArrayList<String>();

	private IProcessorListener listener;
	
	private boolean ready;
	
	/** You have to import org.apache.log4j via the pax-logging bundle to use this logger */
//	protected static final Logger log = Logger.getLogger(IProcessor.class);
	
	@Override
	public Properties getProperties() {
		return properties;
	}
	
	@Override
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	@Override
	public void setProperty(String key, String value) {
		String oldValue = (String) properties.setProperty(key, value);
		String[] validate = validate();
		if(validate != null && validate.length > 0)
			properties.setProperty(key, oldValue);
	}
	
	@Override
	public void result(QueryResult result) throws InterruptedException {
		results.put(result);
		result(results);
	}
	
	@Override
	public void query(QueryTicket ticket) throws InterruptedException {
		tickets.put(ticket);	
		query(tickets);
	}
	
	/* ======================== */
	/* == utility operations == */
	/* ======================== */
	
	/**
	 * 
	 * @param name
	 * @param input
	 * @param processor
	 * @throws InterruptedException
	 */
	protected void fireQuery(String name,Instance input, IProcessor processor) throws InterruptedException {
		fireQuery(name, input, processor,null);
	}
	
	/**
	 * 
	 * @param name
	 * @param input
	 * @param processor
	 * @param headers
	 * @throws InterruptedException
	 */
	protected void fireQuery(String name, Instance input, IProcessor processor, Instances[] headers) throws InterruptedException {
		QueryTicket ticket = new QueryTicket(this, input, headers, name);
		processor.query(ticket);
	}
	
	/**
	 * 
	 * @param name
	 * @param loader
	 * @param structureOnly
	 * @throws IOException
	 */
	protected void fireQuery(String name, ILoader loader, boolean structureOnly) throws IOException {
		QueryTicket ticket = new QueryTicket(this, null, name);
		if(structureOnly)
			loader.getStructure(ticket);
		else
			loader.getDataSet(ticket);
	}
	
	protected void fireProcessorStateChanged() {
		if(listener != null)
			listener.processorChanged(this);
	}
	
	protected void generateClassLabels(Instances dataset) {
		int classIndex = Results.guessClassIndex(dataset);
		if(classIndex >= 0) {
			Attribute attribute = dataset.attribute(classIndex);
			labels = Collections.list(attribute.enumerateValues());
		}
	}
	
	/* ========================= */
	/* ==== to be overriden ==== */
	/* ========================= */
	
	/**
	 * <p>The delegate method which is called when a {@link QueryResult} was accepted</p>
	 *  
	 * @param results
	 */
	protected void result(BlockingQueue<QueryResult> results) {
		results.clear();
		throw new UnsupportedOperationException("This processor doesn't handle results");
	}
	
	/**
	 * <p>The delegate method which is called when a {@link QueryTicket} was accepted</p>
	 * 
	 * @param tickets
	 */
	protected void query(BlockingQueue<QueryTicket> tickets) {
		tickets.clear();
		throw new UnsupportedOperationException("This processor doesn't handle queries");
	}
	
	/* ========================= */
	/* == standard operations == */
	/* ========================= */
	
	@Override
	public void setProcessorListener(IProcessorListener listener) {
		this.listener = listener;		
	}
	
	@Override
	public List<String> getClassLabels() {
		return labels;
	}
	
	@Override
	public boolean isReady() {
		return ready;
	}
	
	public synchronized void setReady(boolean ready) {
		this.ready = ready;
	}
	
	/**
	 * @return {@link Capabilities}
	 * @see Capabilities
	 * @see AbstractClassifier
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities result = new Capabilities(this);
		result.enableAll();
		return result;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + hashCode();
	}

}
