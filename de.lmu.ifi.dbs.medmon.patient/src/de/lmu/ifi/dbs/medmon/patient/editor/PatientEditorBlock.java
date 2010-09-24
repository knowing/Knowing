package de.lmu.ifi.dbs.medmon.patient.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;
import de.lmu.ifi.dbs.medmon.patient.Activator;
import de.lmu.ifi.dbs.medmon.patient.provider.PatientContentProvider;
import de.lmu.ifi.dbs.medmon.patient.provider.PatientLabelProvider;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.patient.service.component.PatientService;

public class PatientEditorBlock extends MasterDetailsBlock {
	
	private TableViewer viewer;
	
	private final IPatientService patientService;
	

	public PatientEditorBlock() {
		patientService = Activator.getPatientService();
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm,
			Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();

		/* Patient Section */
		Section sSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		sSection.setText("Patienten");
		sSection.marginWidth = 10;
		sSection.marginHeight = 5;

		Composite sensorClient = toolkit.createComposite(sSection, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		sensorClient.setLayout(layout);
		Table t = toolkit.createTable(sensorClient, SWT.NULL);
		GridData data = new GridData(GridData.FILL_BOTH);
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
		viewer = new TableViewer(t);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
				patientService.setSelection(event.getSelection()); //Set patient 
			}
		});
		viewer.setContentProvider(new PatientContentProvider());
		viewer.setLabelProvider(new PatientLabelProvider());
		viewer.setInput(SampleDataFactory.getData());

	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(Patient.class, new PatientDetailsPage());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		Action haction = new Action("hor", Action.AS_RADIO_BUTTON) { //$NON-NLS-1$
			public void run() {
				sashForm.setOrientation(SWT.HORIZONTAL);
				form.reflow(true);
			}
		};
		haction.setChecked(true);
		haction.setToolTipText("Horizontal"); //$NON-NLS-1$
		haction.setImageDescriptor(Activator.getImageDescriptor("icons/th_horizontal.gif"));
		Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) { //$NON-NLS-1$
			public void run() {
				sashForm.setOrientation(SWT.VERTICAL);
				form.reflow(true);
			}
		};
		vaction.setChecked(false);
		vaction.setToolTipText("Vertical"); //$NON-NLS-1$
		vaction.setImageDescriptor(Activator.getImageDescriptor("icons/th_vertical.gif"));
		form.getToolBarManager().add(haction);
		form.getToolBarManager().add(vaction);
	}
	


}
