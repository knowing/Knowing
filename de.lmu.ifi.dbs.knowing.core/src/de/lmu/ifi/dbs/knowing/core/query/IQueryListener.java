package de.lmu.ifi.dbs.knowing.core.query;

/**
 * Listener Interface for inter-processor/loader communication.
 *  
 * @author Nepomuk Seiler
 *
 */
public interface IQueryListener {

	/**
	 * 
	 * @param result - answer for a query
	 * @throws InterruptedException 
	 */
	void result(QueryResult result) throws InterruptedException;
}
