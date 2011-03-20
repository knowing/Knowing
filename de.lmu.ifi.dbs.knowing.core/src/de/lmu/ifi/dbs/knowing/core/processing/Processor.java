package de.lmu.ifi.dbs.knowing.core.processing;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

import de.lmu.ifi.dbs.knowing.core.query.QueryResult;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

/**
 * <p>Implements some query methods and synchronizes them, so a robust and reliable<br>
 * service will be generated.</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.4
 */
public abstract class Processor implements IProcessor {

	protected final Properties properties = new Properties();

	protected final BlockingQueue<QueryResult> results = new LinkedBlockingQueue<QueryResult>();
	
	protected final BlockingQueue<QueryTicket> tickets = new ArrayBlockingQueue<QueryTicket>(1000, true);
	
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
	public boolean isAlive() {
		return true;
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
	 * @param resultSet
	 * @throws InterruptedException
	 */
	protected void fireQuery(String name, Instance input, IProcessor processor, Instances resultSet) throws InterruptedException {
		QueryTicket ticket = new QueryTicket(this, input, resultSet, name);
		processor.query(ticket);
	}
	
	/**
	 * 
	 * @param name
	 * @param loader
	 * @param structureOnly
	 * @throws IOException
	 */
	protected void fireQuery(String name,ILoader loader, boolean structureOnly) throws IOException {
		QueryTicket ticket = new QueryTicket(this, null, name);
		if(structureOnly)
			loader.getStructure(ticket);
		else
			loader.getDataSet(ticket);
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
	
	/**
	 * <p>The delegate method which is called when a {@link QueryResult} was accepted</p>
	 *  
	 * @param results
	 */
	protected abstract void result(BlockingQueue<QueryResult> results);
	
	/**
	 * <p>The delegate method which is called when a {@link QueryTicket} was accepted</p>
	 * 
	 * @param tickets
	 */
	protected abstract void query(BlockingQueue<QueryTicket> tickets);
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + hashCode();
	}

}
