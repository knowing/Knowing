package de.lmu.ifi.dbs.medmon.sensor.ui.pages;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.sensor.controller.ManagementController;
import de.lmu.ifi.dbs.medmon.sensor.ui.Activator;
import de.lmu.ifi.dbs.medmon.sensor.ui.provider.DataContentProvider;
import de.lmu.ifi.dbs.medmon.sensor.ui.provider.DataLabelProvider;
import de.lmu.ifi.dbs.medmon.sensor.ui.provider.SensorDetailPageProvider;
import de.lmu.ifi.dbs.medmon.sensor.ui.viewer.SensorTableViewer;

import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;

public class SensorMasterBlock extends MasterDetailsBlock {
	
	public static ManagementController controller;
	
	public SensorMasterBlock() {
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = toolkit.createScrolledForm(parent);
		ColumnLayout columnLayout = new ColumnLayout();
		columnLayout.maxNumColumns = 1;
		form.getBody().setLayout(columnLayout);
		
		/* SensorSection */
		Section sensorSection = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.EXPANDED);
		sensorSection.setLayoutData(new ColumnLayoutData(200, 250));
		sensorSection.setText("Sensor");
		sensorSection.marginWidth = 10;
		sensorSection.marginHeight = 5;

		Composite sensorClient = toolkit.createComposite(sensorSection, SWT.WRAP);
		sensorClient.setLayout(new GridLayout(3, false));
		sensorSection.setClient(sensorClient);

		Table table = toolkit.createTable(sensorClient, SWT.MULTI | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		TableViewer sensorViewer = new SensorTableViewer(table);
		final SectionPart sensorPart = new SectionPart(sensorSection);
		managedForm.addPart(sensorPart);
		
		sensorViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(sensorPart, event.getSelection());
				IPatientService service = Activator.getPatientService();
				service.setSelection(event.getSelection());
			}
		});
		sensorViewer.setInput(this);
			
		ImageHyperlink openSensorLink = toolkit.createImageHyperlink(sensorClient, SWT.NONE);
		openSensorLink.setText("Sensor oeffnen");
		openSensorLink.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN,	IMedmonConstants.IMG_OPEN_16));
		
		ImageHyperlink refreshLink = toolkit.createImageHyperlink(sensorClient, SWT.NONE);
		refreshLink.setText("Aktualisieren");
		refreshLink.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN,IMedmonConstants.IMG_REFRESH_16));
		new Label(sensorClient, SWT.NONE);
		toolkit.paintBordersFor(sensorClient);

		/* DataSection */
		Section dataSection = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.EXPANDED);
		//TODO Will not resize
		dataSection.setLayoutData(new ColumnLayoutData(200, 300));
		dataSection.setText("Daten");
		dataSection.marginWidth = 10;
		dataSection.marginHeight = 5;

		Composite dataClient = toolkit.createComposite(dataSection, SWT.WRAP);
		
		dataSection.setClient(dataClient);
		dataClient.setLayout(new GridLayout(3, false));

		TreeViewer dataViewer = new TreeViewer(dataClient, SWT.BORDER | SWT.MULTI);
		dataViewer.setContentProvider(new DataContentProvider());
		dataViewer.setLabelProvider(new DataLabelProvider());
		Tree dataTree = dataViewer.getTree();
		GridData gd_dataTree = new GridData(GridData.FILL_BOTH);
		gd_dataTree.horizontalSpan = 3;
		dataTree.setLayoutData(gd_dataTree);
		toolkit.paintBordersFor(dataTree);

		final SectionPart spart = new SectionPart(dataSection);
		managedForm.addPart(spart);

		dataViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
				IPatientService service = Activator.getPatientService();
				service.setSelection(event.getSelection());
			}
		});

		controller = new ManagementController(dataViewer, sensorViewer);
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageProvider(new SensorDetailPageProvider());		
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {

	}

}
