package de.lmu.ifi.dbs.medmon.medic.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.medic.ui.pages.DPUMasterBlock;

public class MedicProcessingView extends ViewPart {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.ui.views.MedicProcessingView"; //$NON-NLS-1$
	private FormToolkit toolkit;
	private ManagedForm managedForm;
	

	public MedicProcessingView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		managedForm = createManagedForm(parent);
		toolkit = managedForm.getToolkit();
		managedForm.getToolkit().adapt(parent);
		Composite container = managedForm.getForm().getBody();
		container.setLayout(new GridLayout(2, false));
		
		DPUMasterBlock block = new DPUMasterBlock();
		block.createContent(managedForm);
		
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	public void dispose() {
		managedForm.dispose();
		super.dispose();
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

	@Override
	public void setFocus() {
		//
	}

}
