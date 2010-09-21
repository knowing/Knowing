package de.lmu.ifi.dbs.medmon.algorithm.editor;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.eclipse.swt.widgets.Label;

import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.algorithm.ui.AlgorithmDetailComposite;
import org.eclipse.swt.layout.FillLayout;

public class AlgorithmDetailsPage implements IDetailsPage {
	
	public AlgorithmDetailsPage() {
	}

	private IManagedForm managedForm;

	@Override
	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void commit(boolean onSave) {

	}

	@Override
	public boolean setFormInput(Object input) {
		System.out.println("SetFormInput AlgorithmDetailsPage");
		return false;
	}

	@Override
	public void setFocus() {
		System.out.println("SetFocus AlgorithmDetailsPage");
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {

	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		System.out.println("SetSelectionChanged AlgorithmDetailsPage: " + selection);
		if(!selection.isEmpty() && selection instanceof IStructuredSelection) {
			IStructuredSelection sel =(IStructuredSelection)selection;
			ISensorDataAlgorithm algorithm = (ISensorDataAlgorithm)sel.getFirstElement();
		}
		
	}

	@Override
	public void createContents(Composite parent) {
		System.out.println("CreateContents AlgorithmDetaislPage");
		FormToolkit toolkit = managedForm.getToolkit();	
		parent.setLayout(new ColumnLayout());
		
		/* Sensor DB */
		
		Section pSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		pSection.setText("Eigenschaften");
		pSection.setDescription("Algorithmus konfigurieren");
		
		AlgorithmDetailComposite pClient = new AlgorithmDetailComposite(pSection, SWT.NONE);
		toolkit.paintBordersFor(pClient.getControl());
		pSection.setClient(pClient.getControl());
		
	}
}
