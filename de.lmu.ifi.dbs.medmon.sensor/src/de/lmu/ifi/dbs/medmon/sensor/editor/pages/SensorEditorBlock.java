package de.lmu.ifi.dbs.medmon.sensor.editor.pages;

import java.util.Set;

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
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.sensor.controller.ManagementController;
import de.lmu.ifi.dbs.medmon.sensor.viewer.SensorTableViewer;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Hyperlink;

public class SensorEditorBlock extends MasterDetailsBlock {
	public SensorEditorBlock() {
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();

		/* Sensor Section */
		Section sSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		sSection.setText("Sensor");
		sSection.marginWidth = 10;
		sSection.marginHeight = 5;

		Composite sClient = toolkit.createComposite(sSection, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		sClient.setLayout(layout);
		sSection.setClient(sClient);
		
		
		
		final SectionPart spart = new SectionPart(sSection);
		managedForm.addPart(spart);
		TableViewer viewer = createTableViewer(sClient, toolkit);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});
		viewer.setInput(SampleDataFactory.getSensorDataArray());
		
		ManagementController controller = new ManagementController(viewer);
		
		ImageHyperlink importLink = toolkit.createImageHyperlink(sClient, SWT.NONE);
		toolkit.paintBordersFor(importLink);
		importLink.setText("Import");
		importLink.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_ARROW_DOWN_16));
		importLink.setHref(ManagementController.IMPORT);
		importLink.addHyperlinkListener(controller);
		
		ImageHyperlink exportLink = toolkit.createImageHyperlink(sClient, SWT.NONE);
		toolkit.paintBordersFor(exportLink);
		exportLink.setText("Export");
		exportLink.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_ARROW_UP_16));
		exportLink.setHref(ManagementController.EXPORT);
		exportLink.addHyperlinkListener(controller);
		
		ImageHyperlink deleteLink = toolkit.createImageHyperlink(sClient, SWT.NONE);
		toolkit.paintBordersFor(deleteLink);
		deleteLink.setText("Löschen");
		deleteLink.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_REMOVE_16));
		deleteLink.setHref(ManagementController.DELETE);
		deleteLink.addHyperlinkListener(controller);
		
		
	}
	
	private TableViewer createTableViewer(Composite parent, FormToolkit toolkit) {
		Table table = toolkit.createTable(parent, SWT.MULTI | SWT.FULL_SELECTION);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		data.heightHint = 20;
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
