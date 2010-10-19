package de.lmu.ifi.dbs.medmon.therapy.pages;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.therapy.core.extensions.IDisease;

public class DiseaseDetailsPage implements IDetailsPage {

	private IManagedForm managedForm;
	private TherapyMasterBlock block;

	/**
	 * Create the details page.
	 */
	public DiseaseDetailsPage() {
		// Create the details page
	}

	/**
	 * Initialize the details page.
	 * @param form
	 */
	public void initialize(IManagedForm form) {
		managedForm = form;
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		parent.setLayout(new FillLayout());
		//		
		Section section = toolkit.createSection(parent,	Section.EXPANDED | Section.TITLE_BAR);
		section.setText("Therapien");
		//
		block = new TherapyMasterBlock();
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		block.createContent(new ManagedForm(toolkit,managedForm.getForm()), composite);
		

		toolkit.paintBordersFor(composite);
		section.setClient(composite);
	}

	public void dispose() {
		// Dispose
	}

	public void setFocus() {
		// Set focus
	}

	private void update() {
		// Update
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object selected = structuredSelection.getFirstElement();
		block.setInput((IDisease)selected);
		update();
	}

	public void commit(boolean onSave) {
		// Commit
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		update();
	}

}
