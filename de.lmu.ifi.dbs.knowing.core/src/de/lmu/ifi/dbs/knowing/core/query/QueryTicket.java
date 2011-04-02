package de.lmu.ifi.dbs.knowing.core.query;

import java.util.Date;

import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;
import weka.core.Instance;
import weka.core.Instances;

/**
 * <p>A QueryTicket represents a single query. The ticket hold information<br>
 * to identify the query by the inquirer. For this purpose a QueryTicket holds<br>
 * a creation-timestamp and a name. </p>
 * 
 * <p>A QueryTicket holds the Instance Object which is used to query the given<br>
 * {@link IProcessor}. If it should use an existing resultset instead of creating<br>
 * a new one, the ticket can hold an {@link Instances} object.</p>
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 *
 */
public class QueryTicket {

	private final long timestamp;
	private final IQueryListener inquirer;
	private final Instance query;
	private final String name;
	
	private Instances[] headers;
	
	
	public QueryTicket(IQueryListener inquirer, Instance query, Instances[] headers, String name) {
		this.timestamp = System.currentTimeMillis();
		this.inquirer = inquirer;
		this.query = query;
		this.headers = headers;
		this.name = name;
	}
	
	public QueryTicket(IQueryListener inquirer, Instance query, Instances header, String name) {
		this.timestamp = System.currentTimeMillis();
		this.inquirer = inquirer;
		this.query = query;
		this.headers = new Instances[] { header };
		this.name = name;
	}

	public QueryTicket(IQueryListener inquirer, Instance query, String name) {
		this(inquirer, query, new Instances[0], name);
	}
	
	public void fireResult(Instances[] results) throws InterruptedException {
		inquirer.result(new QueryResult(results, this));
	}
	
	public void fireResult(Instances result) throws InterruptedException {
		inquirer.result(new QueryResult(result, this));
	}

	public long getTimestamp() {
		return timestamp;
	}

	public IQueryListener getInquirer() {
		return inquirer;
	}
	
	public Instance getQuery() {
		return query;
	}
	
	public Instances[] getHeaders() {
		return headers;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryTicket [timestamp=");
		builder.append(new Date(timestamp));
		builder.append(", inquirer=");
		builder.append(inquirer);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
	
		
}
