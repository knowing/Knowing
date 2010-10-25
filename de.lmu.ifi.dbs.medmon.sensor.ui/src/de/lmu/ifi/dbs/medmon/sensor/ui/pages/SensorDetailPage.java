package de.lmu.ifi.dbs.medmon.sensor.ui.pages;

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

import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.sensor.controller.ManagementController;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;
import de.lmu.ifi.dbs.medmon.sensor.ui.Activator;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class SensorDetailPage implements IDetailsPage {

	private IManagedForm managedForm;
	private Text tName, tTyp, tDescription;

	private ISensor sensor;

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
		composite.setLayout(new GridLayout(4, false));
		
		Label lName = new Label(composite, SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		toolkit.adapt(lName, true, true);
		lName.setText("Name");
		
		tName = toolkit.createText(composite,"", SWT.READ_ONLY);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		data.widthHint = 150;
		tName.setLayoutData(data);
		
		Label lTyp = new Label(composite, SWT.NONE);
		toolkit.adapt(lTyp, true, true);
		lTyp.setText("Typ");
		
		tTyp = toolkit.createText(composite,"", SWT.READ_ONLY);
		GridData gd_text_1 = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_text_1.widthHint = 150;
		tTyp.setLayoutData(gd_text_1);
		
		Group gDescription = new Group(composite, SWT.NONE);
		gDescription.setText("Beschreibung");
		data = new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1);
		data.heightHint = 100;
		gDescription.setLayoutData(data);
		toolkit.adapt(gDescription);
		toolkit.paintBordersFor(gDescription);
		gDescription.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tDescription = new Text(gDescription, SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
		tDescription.setEditable(false);
		toolkit.adapt(tDescription, true, true);

		
		Label label = new Label(composite, SWT.NONE);
		
		label.setImage(Activator.getImageDescriptor("res/vitruvian.jpeg").createImage());
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		toolkit.adapt(label, true, true);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		ImageHyperlink importLink = toolkit.createImageHyperlink(composite, SWT.NONE);
		toolkit.paintBordersFor(importLink);
		importLink.setImage(ResourceManager.getPluginImage("de.lmu.ifi.dbs.medmon.rcp", "icons/24/gtk-go-down.png"));
		importLink.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		importLink.setText("Sensordaten importieren");
		importLink.setHref(ManagementController.IMPORT);
		importLink.addHyperlinkListener(SensorMasterBlock.controller);
	}

	public void dispose() {
		// Dispose
	}

	public void setFocus() {
		// Set focus
	}

	private void update() {
		tName.setText(sensor.getName());
		tTyp.setText(sensor.getVersion());
		tDescription.setText(sensor.getDescription());
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		sensor = (ISensor)structuredSelection.getFirstElement();
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
