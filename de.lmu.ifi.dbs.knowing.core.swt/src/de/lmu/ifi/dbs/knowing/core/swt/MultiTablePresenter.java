/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import weka.core.Instances;

/**
 * <p>MultiTablePresenter holds multiple TablePresenter in a map.<br>
 * The {@link Instances#relationName()} works as a key.</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 23.03.2011
 */
public class MultiTablePresenter extends SWTPresenter {

	private TabFolder tabFolder;
	
	private Map<String, TablePresenter> tables = new HashMap<String, TablePresenter>();
	
	@Override
	protected void createControl(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.TOP);
	}

	@Override
	protected void createContent(Instances dataset) {
		TablePresenter presenter = null;
		if(!tables.containsKey(dataset.relationName())) {
			//Create new container
			presenter = new TablePresenter();
			presenter.createContainer(createTab(dataset.relationName()));
			tables.put(dataset.relationName(), presenter);
		} else {
			presenter = tables.get(dataset.relationName());
		}
		//finally add data
		presenter.createContent(dataset);
	}
	
	/**
	 * <p>Creates a tab with the given name and returns <br>
	 * the composite which is controlled by the tabitem.</p>
	 * 
	 * @param name - name of the tab
	 * @return tab content composite
	 */
	public Composite createTab(String name) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(name);
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		tabItem.setControl(composite);
		return composite;
	}
	
	@Override
	public String getName() {
		return "Multi Table";
	}

}
