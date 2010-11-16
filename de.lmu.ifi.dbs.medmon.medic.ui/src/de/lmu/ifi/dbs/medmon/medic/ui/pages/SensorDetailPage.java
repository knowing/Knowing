package de.lmu.ifi.dbs.medmon.medic.ui.pages;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.medic.ui.controller.SensorManagementController;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class SensorDetailPage implements IDetailsPage {

	private IManagedForm managedForm;
	private Text tName, tTyp, tDescription;

	private ISensor sensor;
	private Text text;

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
		ColumnLayout layout_parent = new ColumnLayout();
		layout_parent.maxNumColumns = 2;
		parent.setLayout(layout_parent);
		//		
		Section sensorSection = toolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		sensorSection.setText("Sensor");

		//
		Composite sensorComposite = toolkit.createComposite(sensorSection, SWT.NONE);
		//toolkit.paintBordersFor(sensorComposite);
		sensorSection.setClient(sensorComposite);
		sensorComposite.setLayout(new GridLayout(4, false));
		
		Label lName = new Label(sensorComposite, SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		toolkit.adapt(lName, true, true);
		lName.setText("Name:");
		
		tName = toolkit.createText(sensorComposite,"", SWT.READ_ONLY);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		data.widthHint = 150;
		tName.setLayoutData(data);
		
		toolkit.createLabel(sensorComposite, "Typ:");

		
		tTyp = toolkit.createText(sensorComposite,"", SWT.READ_ONLY);
		data = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		data.widthHint = 150;
		tTyp.setLayoutData(data);
		
		Label lData = toolkit.createLabel(sensorComposite, "Daten:", SWT.NONE);
		lData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		text = toolkit.createText(sensorComposite, "New Text", SWT.READ_ONLY);
		text.setText("00:03:46");
		text.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		Label lStatus = toolkit.createLabel(sensorComposite, "Status:", SWT.NONE);
		
		Label lStatusImage = toolkit.createLabel(sensorComposite, "", SWT.NONE);
		lStatusImage.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_APPLY_24));
		lStatusImage.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		
		Group gDescription = new Group(sensorComposite, SWT.NONE);
		gDescription.setText("Beschreibung");
		data = new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1);
		data.heightHint = 100;
		gDescription.setLayoutData(data);
		toolkit.adapt(gDescription);
		gDescription.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tDescription = new Text(gDescription, SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
		tDescription.setEditable(false);
		toolkit.adapt(tDescription, true, true);
		
		Composite buttonComposite = toolkit.createComposite(sensorComposite, SWT.NONE);
		buttonComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		data = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 4, 1);
		data.verticalIndent = 15;
		buttonComposite.setLayoutData(data);
		toolkit.paintBordersFor(buttonComposite);
		
		Button bPreview = toolkit.createButton(buttonComposite, "Vorschau", SWT.NONE);
		bPreview.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_IMAGE_16));
		bPreview.addListener(SWT.Selection, SensorMasterBlock.controller);
		bPreview.setData(SensorManagementController.IMPORT);
		
		Button bImportAll = toolkit.createButton(buttonComposite, "Alles importieren", SWT.NONE);
		bImportAll.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_ARROW_DOWN_16));
		bImportAll.setEnabled(false);
				
		Button bFormatSensor = toolkit.createButton(buttonComposite, "Formatieren", SWT.NONE);
		bFormatSensor.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_REMOVE_16));
		bFormatSensor.setEnabled(false);
		
	}
	
	private void update() {
		tName.setText(sensor.getName());
		tTyp.setText(sensor.getVersion());
		tDescription.setText(sensor.getDescription());
	}

	@Override
	public void dispose() {
		// Dispose
	}

	@Override
	public void setFocus() {
		// Set focus
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		sensor = (ISensor)structuredSelection.getFirstElement();
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
