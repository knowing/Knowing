/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.processing;

import java.util.List;

import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

/**
 * @author Nepomuk Seiler
 * @version 0.4
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
