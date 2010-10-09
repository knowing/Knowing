package de.lmu.ifi.dbs.medmon.sensor.editor.pages;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.sensor.controller.ManagementController;
import de.lmu.ifi.dbs.medmon.sensor.provider.DataContentProvider;
import de.lmu.ifi.dbs.medmon.sensor.provider.DataLabelProvider;
import de.lmu.ifi.dbs.medmon.sensor.viewer.SensorTableViewer;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ColumnLayout;

public class SensorMasterBlock extends MasterDetailsBlock {
	public SensorMasterBlock() {
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = toolkit.createScrolledForm(parent);

		/* Sensor Section */
		ColumnLayout columnLayout = new ColumnLayout();
		columnLayout.maxNumColumns = 2;
		form.getBody().setLayout(columnLayout);
		Section sSection = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.EXPANDED);
		sSection.setText("Sensor");
		sSection.marginWidth = 10;
		sSection.marginHeight = 5;

		Composite sClient = toolkit.createComposite(sSection, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		sClient.setLayout(layout);
		sSection.setClient(sClient);

		TableViewer sensorViewer = createTableViewer(sClient, toolkit);
		sensorViewer.setInput(SampleDataFactory.getSensorDataArray());

		ManagementController controller = new ManagementController(sensorViewer);

		Section dataSection = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.EXPANDED);
		dataSection.setText("Daten");
		dataSection.marginWidth = 10;
		dataSection.marginHeight = 5;

		Composite dataClient = toolkit.createComposite(dataSection, SWT.WRAP);
		GridLayout data_layout = new GridLayout();
		data_layout.numColumns = 3;
		layout.numColumns = 3;
		dataClient.setLayout(data_layout);
		dataSection.setClient(dataClient);

		TreeViewer dataViewer = new TreeViewer(dataClient, SWT.BORDER | SWT.MULTI);
		dataViewer.setContentProvider(new DataContentProvider());
		dataViewer.setLabelProvider(new DataLabelProvider());
		Tree tree = dataViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		toolkit.paintBordersFor(tree);

		final SectionPart spart = new SectionPart(dataSection);
		managedForm.addPart(spart);

		dataViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});
		dataViewer.setInput(SampleDataFactory.getSensorDataArray());

		ImageHyperlink importLink = toolkit.createImageHyperlink(dataClient, SWT.NONE);
		toolkit.paintBordersFor(importLink);
		importLink.setText("Import");
		importLink.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN,	IMedmonConstants.IMG_ARROW_DOWN_16));
		importLink.setHref(ManagementController.IMPORT);
		importLink.addHyperlinkListener(controller);

		ImageHyperlink exportLink = toolkit.createImageHyperlink(dataClient, SWT.NONE);
		toolkit.paintBordersFor(exportLink);
		exportLink.setText("Export");
		exportLink.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN,	IMedmonConstants.IMG_ARROW_UP_16));
		exportLink.setHref(ManagementController.EXPORT);
		exportLink.addHyperlinkListener(controller);

		ImageHyperlink deleteLink = toolkit.createImageHyperlink(dataClient, SWT.NONE);
		toolkit.paintBordersFor(deleteLink);
		deleteLink.setText("Loeschen");
		deleteLink.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_REMOVE_16));
		deleteLink.setHref(ManagementController.DELETE);
		deleteLink.addHyperlinkListener(controller);

	}

	private TableViewer createTableViewer(Composite parent, FormToolkit toolkit) {
		Table table = toolkit.createTable(parent, SWT.MULTI | SWT.FULL_SELECTION);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessVerticalSpace = false;
		data.horizontalSpan = 3;
		data.heightHint = 150;
		data.widthHint = 130;
		table.setLayoutData(data);
		toolkit.paintBordersFor(parent);

		return new SensorTableViewer(table);
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(Data.class, new SensorDetailPage());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {

	}

}
