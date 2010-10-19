package de.lmu.ifi.dbs.medmon.algorithm.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.algorithm.ui.editor.AlgorithmEditorBlock;

public class ManagementView extends ViewPart {

	public static final String ID = "de.lmu.ifi.dbs.medmon.patient.Management";
	
	private ManagedForm managedForm;
	
	public ManagementView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		managedForm = createManagedForm(parent);
		managedForm.getForm().getBody().setLayout(new FillLayout());
		managedForm.getToolkit().adapt(parent);

		initialize(managedForm);
	}
	
	public void initialize(ManagedForm managedForm) {
		AlgorithmEditorBlock block = new AlgorithmEditorBlock();
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

}
