package de.lmu.ifi.dbs.medmon.patient.editor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class GeneralEditorBlock extends MasterDetailsBlock {

	@Override
	protected void createMasterPart(final IManagedForm managedForm,
			Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();

		GridLayout fLayout = (GridLayout) managedForm.getForm().getBody()
				.getLayout();
		fLayout.makeColumnsEqualWidth = false;
		fLayout.numColumns = 2;

		/* General Information */
		Section gSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		gSection.setText("Patienteninformationen");
		gSection.marginWidth = 10;
		gSection.marginHeight = 5;

		Composite gClient = toolkit.createComposite(gSection, SWT.WRAP);
		GridLayout gLayout = new GridLayout(2, false);
		gLayout.marginWidth = 5;
		gLayout.marginHeight = 5;
		gClient.setLayout(gLayout);
	
		toolkit.createLabel(gClient, "Name");
		Text firstname = toolkit.createText(gClient, "", SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		firstname.setLayoutData(data);
		toolkit.createLabel(gClient, "Nachname");
		Text lastname = toolkit.createText(gClient, "", SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		lastname.setLayoutData(data);
		
		gSection.setClient(gClient);
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 3;
		gSection.setLayoutData(data);

		/* Sensordata Section */
		Section sSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		sSection.setText("Sensor Datensaetze");
		sSection.marginWidth = 10;
		sSection.marginHeight = 5;

		Composite sensorClient = toolkit.createComposite(sSection, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		sensorClient.setLayout(layout);
		Table t = toolkit.createTable(sensorClient, SWT.NULL);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 20;
		data.widthHint = 100;
		t.setLayoutData(data);
		toolkit.paintBordersFor(sensorClient);
		Button b = toolkit.createButton(sensorClient, "laden", SWT.PUSH); //$NON-NLS-1$
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		b.setLayoutData(data);
		sSection.setClient(sensorClient);
		final SectionPart spart = new SectionPart(sSection);
		managedForm.addPart(spart);
		TableViewer viewer = new TableViewer(t);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});

	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// TODO Auto-generated method stub

	}

}
