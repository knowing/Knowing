/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.processing;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import weka.core.Instances;

import de.lmu.ifi.dbs.knowing.core.query.IQueryListener;
import de.lmu.ifi.dbs.knowing.core.query.QueryResult;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 21.03.2011
 */
public abstract class Presenter<T> implements IPresenter<T>,IQueryListener {

	private final BlockingQueue<QueryResult> results = new LinkedBlockingQueue<QueryResult>();
	
	@Override
	public void buildPresentation(ILoader loader) throws IOException {
		loader.getDataSet(new QueryTicket(this, null, getName()));
	}
	
	@Override
	public void buildPresentation(IResultProcessor processor) throws InterruptedException {
		List<String> classLabels = processor.getClassLabels();
		Instances model = getModel(classLabels);
		processor.queryResults(new QueryTicket(this, null, model, getName()));
	}
	
	@Override
	public void result(QueryResult result) throws InterruptedException {
		results.put(result);
		createPresentation(results);
	}
	
	/**
	 * 
	 * @param results
	 */
	abstract protected void createPresentation(BlockingQueue<QueryResult> results);
}
