/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.03.2011
 */
public class PresenterView extends ViewPart {

	public static final String ID = "de.lmu.ifi.dbs.knowing.core.swt.presenterView";
	
	private TabFolder tabFolder;

	@Override
	public void createPartControl(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.BOTTOM);
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
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		tabItem.setControl(composite);
		return composite;
	}
	
	public void clearTabs() {
		TabItem[] items = tabFolder.getItems();
		for (TabItem tabItem : items) 
			tabItem.dispose();
	}

	@Override
	public void setFocus() {
		tabFolder.setFocus();
	}

}
