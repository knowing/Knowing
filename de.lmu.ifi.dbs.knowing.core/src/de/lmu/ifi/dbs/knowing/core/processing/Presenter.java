/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.processing;

import java.io.IOException;

import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.query.IQueryListener;
import de.lmu.ifi.dbs.knowing.core.query.QueryResult;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.03.2011
 */
public abstract class Presenter<T> implements IPresenter<T>,IQueryListener {

	@Override
	public void buildPresentation(ILoader loader) throws IOException {
		loader.getDataSet(new QueryTicket(this, null, getName()));
	}
	
	@Override
	public void buildPresentation(IResultProcessor processor) {
		createPresentation(processor.getResult());
	}
	
	@Override
	public void result(QueryResult result) throws InterruptedException {
		createPresentation(result.getTicket().getResultSet());
	}
	
	abstract protected void createPresentation(Instances dataset);
}
