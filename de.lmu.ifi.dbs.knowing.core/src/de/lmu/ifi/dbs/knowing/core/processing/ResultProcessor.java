/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.processing;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 24.03.2011
 */
public abstract class ResultProcessor extends Processor implements IResultProcessor {

	private BlockingQueue<QueryTicket> resultTickets = new ArrayBlockingQueue<QueryTicket>(1000, true);


	@Override
	public void queryResults(QueryTicket ticket) throws InterruptedException {
		resultTickets.put(ticket);
		queryResults(resultTickets);
	}

	abstract protected void queryResults(BlockingQueue<QueryTicket> resultTickets);

}
