package de.lmu.ifi.dbs.knowing.core.query;

import weka.core.Instances;

/**
 * Wrapper class for a result of a query
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 */
public class QueryResult {

	private final QueryTicket ticket;
	
	private final Instances[] results;
		
	public QueryResult(Instances result, QueryTicket ticket) {
		this(new Instances[] {result}, ticket);
	}
	
	public QueryResult(Instances[] results, QueryTicket ticket) {
		this.results = results;
		this.ticket = ticket;
	}

	public Instances[] getResults() {
		return results;
	}

	public QueryTicket getTicket() {
		return ticket;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryResult [ticket=");
		builder.append(ticket);
		builder.append("]");
		return builder.toString();
	}
	
	
		
	
}
