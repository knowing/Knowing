package de.lmu.ifi.dbs.medmon.medic.ui.pages;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.medic.core.unit.MedicProcessingUnit;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.DPULabelProvider;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;

public class MPUDetailsPage implements IDetailsPage {

	private IManagedForm managedForm;
	
	private MedicProcessingUnit mpu;
	private Text tDescription;

	private TableViewer dpuViewer;

	/**
	 * Create the details page.
	 */
	public MPUDetailsPage() {
		// Create the details page
	}

	/**
	 * Initialize the details page.
	 * @param form
	 */
	@Override
	public void initialize(IManagedForm form) {
		managedForm = form;
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	@Override
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		parent.setLayout(new FillLayout());
		//		
		Section section = toolkit.createSection(parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		section.setText("MPU Details");
		//
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		Label lDescription = toolkit.createLabel(composite, "Beschreibung", SWT.NONE);
		lDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		
		tDescription = toolkit.createText(composite, "New Text", SWT.NONE);
		tDescription.setText("");
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		data.heightHint = 75;
		tDescription.setLayoutData(data);
		
		Label lProcessor = toolkit.createLabel(composite, "Verfahren", SWT.NONE);
		lProcessor.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		
		dpuViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		dpuViewer.setContentProvider(new ArrayContentProvider());
		dpuViewer.setLabelProvider(new DPULabelProvider());
		dpuViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
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
		MedicProcessingUnit mpu = (MedicProcessingUnit) structuredSelection.getFirstElement();
		List<DataProcessingUnit> dpus = mpu.getDpus();
		dpuViewer.setInput(dpus.toArray());
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
