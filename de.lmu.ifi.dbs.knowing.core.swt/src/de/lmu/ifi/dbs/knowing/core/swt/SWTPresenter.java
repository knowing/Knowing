/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt;

import java.util.concurrent.BlockingQueue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.processing.Presenter;
import de.lmu.ifi.dbs.knowing.core.query.QueryResult;

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 21.03.2011
 */
public abstract class SWTPresenter extends Presenter<Composite> {

	private Composite composite;

	@Override
	public Object createContainer(Composite parent) {
		//TODO SWTPresenter.createContainer must be synchronized with the UI Thread
		if (composite != null && !composite.isDisposed())
			dispose();
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		createControl(composite);
		return composite;
	}

	public void dispose() {
		composite.dispose();
	}

	@Override
	protected void createPresentation(final BlockingQueue<QueryResult> results) {
		if(composite == null)
			return;
		composite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				//Avoid invalid thread access
				QueryResult result = results.poll();
				while(result != null) {
					Instances[] datasets = result.getResults();
					for (Instances dataset : datasets) {
						createContent(dataset);
					}
					result = results.poll();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.lmu.ifi.dbs.knowing.core.view.IPresenter#getContainerClass()
	 */
	@Override
	public String getContainerClass() {
		return Composite.class.getName();
	}

	/**
	 * 
	 * @param parent
	 */
	abstract protected void createControl(Composite parent);

	/**
	 * 
	 * @param dataset
	 */
	abstract protected void createContent(Instances dataset);
}
