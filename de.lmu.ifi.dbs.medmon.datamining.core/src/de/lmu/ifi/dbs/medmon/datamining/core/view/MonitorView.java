package de.lmu.ifi.dbs.medmon.datamining.core.view;

import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.internal.Processor;

public class MonitorView extends ViewPart implements IPropertyChangeListener{

	public static final String ID = "de.lmu.ifi.dbs.medmon.datamining.core.view.MonitorView"; //$NON-NLS-1$
	
	private TabFolder tabFolder;
	

	public MonitorView() {
		Processor.add(this);
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.NONE);

		createActions();
		initializeToolBar();
		initializeMenu();
	}
	
	private void createTabItems(Map<String, IAnalyzedData> data, TabFolder tabFolder) {
		Set<String> keys = data.keySet();
		for (String key : keys) {
			if(key.equals(IAlgorithm.DEFAULT_DATA))
				continue;
			TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
			tabItem.setText(key);
			Composite composite = new Composite(tabFolder, SWT.NONE);
			composite.setLayout(new FillLayout(SWT.HORIZONTAL));
			data.get(key).createContent(composite);
			tabItem.setControl(composite);
		}
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		tabFolder.setFocus();
	}
	

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		tabFolder.getDisplay().syncExec(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("TabFolder: " + tabFolder);
				if (tabFolder == null)
					return;

				for (TabItem item : tabFolder.getItems())
					item.dispose();
				if (event.getNewValue() != null) {
					Map<String, IAnalyzedData> data = (Map<String, IAnalyzedData>) event.getNewValue();
					createTabItems(data, tabFolder);
				}
				tabFolder.layout(true);
				
			}
		});

	}

}
