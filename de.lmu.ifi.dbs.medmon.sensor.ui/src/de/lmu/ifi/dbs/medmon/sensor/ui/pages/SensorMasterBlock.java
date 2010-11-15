package de.lmu.ifi.dbs.medmon.sensor.ui.pages;

import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Button;

public class SensorMasterBlock extends MasterDetailsBlock {

	public static ManagementController controller;
	private static Logger logger = Logger.getLogger(SensorMasterBlock.class.getName());

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
		sensorSection.setLayoutData(new ColumnLayoutData(200, 180));
		sensorSection.setText("Sensor");

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

		new Label(sensorClient, SWT.NONE);
		
		Button bOpen = toolkit.createButton(sensorClient, "Sensor hinzufuegen", SWT.NONE);
		bOpen.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		bOpen.setText("Sensor oeffnen");
		bOpen.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_ADD_16));

		Button bSensorRefresh = toolkit.createButton(sensorClient, "Aktualisieren", SWT.NONE);
		bSensorRefresh.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		bSensorRefresh.setAlignment(SWT.RIGHT);
		bSensorRefresh.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN,IMedmonConstants.IMG_REFRESH_16));
		toolkit.paintBordersFor(sensorClient);

		/* DataSection */
		Section dataSection = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.EXPANDED);
		dataSection.setDescription("Sensordaten koennen entweder vom Sensor oder aus der Datenbank geladen werden");
		// TODO Will not resize
		dataSection.setLayoutData(new ColumnLayoutData(200, 350));
		dataSection.setText("Daten");
		dataSection.marginWidth = 10;
		dataSection.marginHeight = 5;

		Composite dataClient = toolkit.createComposite(dataSection, SWT.WRAP);

		dataSection.setClient(dataClient);
		dataClient.setLayout(new GridLayout(1, false));

		CTabFolder tabFolder = new CTabFolder(dataClient, SWT.BORDER | SWT.BOTTOM);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toolkit.adapt(tabFolder);
		toolkit.paintBordersFor(tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem sensorTabItem = new CTabItem(tabFolder, SWT.NONE);
		sensorTabItem.setText("Sensor");
		//sensorTabItem.setImage("");

		Composite sensorComposite = toolkit.createComposite(tabFolder, SWT.NONE);
		sensorComposite.setLayout(new GridLayout(1, false));
		
		//TODO Implement MULTI Selection
		TreeViewer dataViewer = new TreeViewer(sensorComposite, SWT.NONE);
		dataViewer.setContentProvider(new DataContentProvider());
		dataViewer.setLabelProvider(new DataLabelProvider());
		dataViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
				
		
		final SectionPart spart = new SectionPart(dataSection);
		dataViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
				IPatientService service = Activator.getPatientService();
				service.setSelection(event.getSelection());
			}
		});
		
		Button bImport = toolkit.createButton(sensorComposite, "Importieren", SWT.NONE);
		bImport.setImage(ResourceManager.getPluginImageDescriptor(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_ARROW_DOWN_16).createImage());
		bImport.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		bImport.setEnabled(false);
		
		sensorTabItem.setControl(sensorComposite);
		
		controller = new ManagementController(dataViewer, sensorViewer);

		CTabItem databaseTabItem = new CTabItem(tabFolder, SWT.NONE);
		databaseTabItem.setText("Datenbank");
		
		Composite dbComposite = toolkit.createComposite(tabFolder, SWT.NONE);
		dbComposite.setLayout(new GridLayout(2, false));
		
		TreeViewer treeViewer = new TreeViewer(dbComposite, SWT.NONE);
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Button bLoad = toolkit.createButton(dbComposite, "Laden", SWT.NONE);
		bLoad.setImage(ResourceManager.getPluginImageDescriptor(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_ARROW_UP_16).createImage());
		bLoad.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false));
		
		Button bRefresh = toolkit.createButton(dbComposite, "Aktualisieren", SWT.NONE);
		bRefresh.setImage(ResourceManager.getPluginImageDescriptor(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_REFRESH_16).createImage());
		bRefresh.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		databaseTabItem.setControl(dbComposite);
		
		tabFolder.setSelection(sensorTabItem);
		managedForm.addPart(spart);
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageProvider(new SensorDetailPageProvider());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		//TODO Create Help-Action calling the HELP View
		Action haction = new Action("help", Action.AS_PUSH_BUTTON) { //$NON-NLS-1$
			public void run() {
				logger.info("Help Action");
			}
		};
		haction.setToolTipText("Help"); //$NON-NLS-1$
		haction.setImageDescriptor(ResourceManager.getPluginImageDescriptor(IMedmonConstants.RCP_PLUGIN,
				IMedmonConstants.IMG_HELP_16));

		form.getToolBarManager().add(haction);
	}
}
