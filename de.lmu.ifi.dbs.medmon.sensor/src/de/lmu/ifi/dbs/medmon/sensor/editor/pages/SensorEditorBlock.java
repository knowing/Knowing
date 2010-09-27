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
import de.lmu.ifi.dbs.medmon.sensor.viewer.SensorTableViewer;

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
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
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
		Set<Data> set = SampleDataFactory.getSensorData();
		viewer.setInput(set.toArray(new Data[set.size()]));
		
	}
	
	private TableViewer createTableViewer(Composite parent, FormToolkit toolkit) {
		Table table = toolkit.createTable(parent, SWT.MULTI | SWT.FULL_SELECTION);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 20;
		data.widthHint = 100;
		table.setLayoutData(data);
		toolkit.paintBordersFor(parent);
		
		TableViewer viewer = new SensorTableViewer(table);
		return viewer;		
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(Data.class, new SensorDetailPage());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		
		
	}

}
