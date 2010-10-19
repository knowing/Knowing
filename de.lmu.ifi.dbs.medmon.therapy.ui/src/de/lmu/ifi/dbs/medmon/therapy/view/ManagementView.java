package de.lmu.ifi.dbs.medmon.therapy.view;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.therapy.pages.DiseaseMasterBlock;

public class ManagementView extends ViewPart {

	public static final String ID = "de.lmu.ifi.dbs.medmon.therapy.view.DiseaseManagement"; //$NON-NLS-1$
	
	private ManagedForm managedForm;

	public ManagementView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		managedForm = createManagedForm(parent);
		managedForm.getForm().getBody().setLayout(new FillLayout());
		managedForm.getToolkit().adapt(parent);

		initialize(managedForm);
		createActions();
		initializeMenu();
		initializeToolBar();
		
	}
	
	public void initialize(ManagedForm managedForm) {
		DiseaseMasterBlock block = new DiseaseMasterBlock();
		block.createContent(managedForm);
	}

	protected ManagedForm createManagedForm(final Composite parent) {
		ManagedForm managedForm = new ManagedForm(parent);
		managedForm.setContainer(this);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		managedForm.getForm().setLayoutData(gridData);
		return managedForm;
	}

	@Override
	public void setFocus() {
		managedForm.getForm().setFocus();
	}
	
	@Override
	public void dispose() {
		managedForm.dispose();
		super.dispose();
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
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}


}
