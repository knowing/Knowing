/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.processing;

import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 20.03.2011
 */
public interface IResultProcessor extends IProcessor {

	/**
	 * 
	 * @param ticket
	 * @throws InterruptedException 
	 */
	void queryResults(QueryTicket ticket) throws InterruptedException;
}
