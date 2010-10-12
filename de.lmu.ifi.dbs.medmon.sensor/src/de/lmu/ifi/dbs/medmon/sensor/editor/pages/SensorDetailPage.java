package de.lmu.ifi.dbs.medmon.sensor.editor.pages;

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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

import de.lmu.ifi.dbs.medmon.sensor.Activator;

public class SensorDetailPage implements IDetailsPage {

	private IManagedForm managedForm;
	private Text text;
	private Text text_1;

	/**
	 * Create the details page.
	 */
	public SensorDetailPage() {
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
		Section sctnSensor = toolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		sctnSensor.setText("Sensor");
		//
		Composite composite = toolkit.createComposite(sctnSensor, SWT.NONE);
		toolkit.paintBordersFor(composite);
		sctnSensor.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		Label lSensor = new Label(composite, SWT.NONE);
		lSensor.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		toolkit.adapt(lSensor, true, true);
		lSensor.setText("Sensor");
		
		text = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 150;
		text.setLayoutData(gd_text);
		toolkit.adapt(text, true, true);
		
		Label lTyp = new Label(composite, SWT.NONE);
		lTyp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		toolkit.adapt(lTyp, true, true);
		lTyp.setText("Typ");
		
		text_1 = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_text_1 = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_text_1.widthHint = 150;
		text_1.setLayoutData(gd_text_1);
		toolkit.adapt(text_1, true, true);
		
		Label label = new Label(composite, SWT.NONE);
		
		label.setImage(Activator.getImageDescriptor("res/vitruvian.jpeg").createImage());
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		toolkit.adapt(label, true, true);
		
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

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
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
