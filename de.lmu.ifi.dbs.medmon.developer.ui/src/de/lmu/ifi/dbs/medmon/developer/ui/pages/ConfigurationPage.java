package de.lmu.ifi.dbs.medmon.developer.ui.pages;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;

public class ConfigurationPage implements IDetailsPage {

	private IManagedForm managedForm;

	/**
	 * Create the details page.
	 */
	public ConfigurationPage() {
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
		Section section = toolkit.createSection(parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		section.setText("Configuration");
		//
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		AlgorithmConfigurationPart part = new AlgorithmConfigurationPart(section);

		managedForm.addPart(part);
	}

	@Override
	public void dispose() {
		// Dispose
	}

	@Override
	public void setFocus() {
		// Set focus
	}

	private void update() {
		// Update
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		XMLDataProcessor processor = (XMLDataProcessor) structuredSelection.getFirstElement();
		managedForm.setInput(processor);
		update();
	}

	@Override
	public void commit(boolean onSave) {
		// Commit
	}
	
	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
		update();
	}

}
