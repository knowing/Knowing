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
import de.lmu.ifi.dbs.medmon.datamining.core.processing.ProcessEvent;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IProcessListener;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.internal.Processor;

public class MonitorView extends ViewPart implements IProcessListener {

	public static final String ID = "de.lmu.ifi.dbs.medmon.datamining.core.view.MonitorView"; //$NON-NLS-1$

	private TabFolder tabFolder;

	public MonitorView() {
		Processor.addProcessListener(this);
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.BOTTOM);

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	private void createTabItems(IAlgorithm algorithm, Map<String, IAnalyzedData> data, TabFolder tabFolder) {
		String[] keys = algorithm.analyzedDataKeys();
		for (String key : keys) {
			if (key.equals(IAlgorithm.DEFAULT_DATA))
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
	public void processChanged(final ProcessEvent event) {
		tabFolder.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				if (tabFolder == null)
					return;

				for (TabItem item : tabFolder.getItems())
					item.dispose();
				if (event.getStatus() == ProcessEvent.FINISHED) {
					createTabItems((IAlgorithm) event.getProcessor(), event.getResult(), tabFolder);
				}
				tabFolder.layout(true);

			}
		});
	}

}
