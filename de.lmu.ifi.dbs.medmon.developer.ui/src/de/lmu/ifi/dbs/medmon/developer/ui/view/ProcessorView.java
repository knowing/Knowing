package de.lmu.ifi.dbs.medmon.developer.ui.view;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;

public class ProcessorView extends ViewPart implements ITabbedPropertySheetPageContributor {

	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.view.ProcessorView"; //$NON-NLS-1$
	private Text text;

	public ProcessorView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));

		Label lSearch = new Label(container, SWT.NONE);
		lSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lSearch.setText("Search");

		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		TableViewer processorViewer = new TableViewer(container, SWT.BORDER);
		processorViewer.setContentProvider(new ArrayContentProvider());
		processorViewer.setLabelProvider(new WorkbenchLabelProvider());
		processorViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		processorViewer.setInput(FrameworkUtil.evaluateDataProcessors());
		getSite().setSelectionProvider(processorViewer);

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	@Override
	public String getContributorId() {
		return getSite().getId();
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class)
			return new TabbedPropertySheetPage(this);
		return super.getAdapter(adapter);
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
		// Set the focus
	}

}
