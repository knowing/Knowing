package de.lmu.ifi.dbs.medmon.medic.ui.pages;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.controller.PatientManagementController;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.PatientContentProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.PatientLabelProvider;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import org.eclipse.swt.widgets.Label;


public class PatientEditorBlock extends MasterDetailsBlock {
	
	private TableViewer viewer;
	
	private final IPatientService patientService;

	private final IStatusLineManager statusLineManager;

	

	public PatientEditorBlock(IStatusLineManager statusLineManager) {
		this.statusLineManager = statusLineManager;
		patientService = Activator.getPatientService();
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm,
			Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();

		/* Patient Section */
		Section pSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		pSection.setText("Patienten");
		pSection.marginWidth = 10;
		pSection.marginHeight = 5;

		Composite pClient = toolkit.createComposite(pSection, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		pClient.setLayout(layout);

		viewer = createTableViewer(pSection, pClient, managedForm);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		data.heightHint = 20;
		data.widthHint = 100;
		viewer.getTable().setLayoutData(data);
		toolkit.paintBordersFor(pClient);
				
		
		PatientManagementController controller = new PatientManagementController(viewer);
		Button add = toolkit.createButton(pClient, "Neu", SWT.PUSH);
		add.setData(PatientManagementController.BUTTON_ADD);
		add.addListener(SWT.Selection, controller);
		
		Button save = toolkit.createButton(pClient, "Speichern", SWT.PUSH);
		save.setData(PatientManagementController.BUTTON_SAVE);
		save.addListener(SWT.Selection, controller);
		
		Button delete = toolkit.createButton(pClient, "Austragen", SWT.PUSH);
		delete.setData(PatientManagementController.BUTTON_DEL);
		delete.addListener(SWT.Selection, controller);
		
		pSection.setClient(pClient);		
		
		Button bRefresh = toolkit.createButton(pClient, "Aktualisieren", SWT.NONE);
		bRefresh.setData(PatientManagementController.BUTTON_REFRESH);
		bRefresh.addListener(SWT.Selection, controller);

	}
	
	private TableViewer createTableViewer(Section section, Composite client, final IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		Table table = toolkit.createTable(client, SWT.NULL);
		TableViewer newViewer = new TableViewer(table);
		
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);
		newViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
				patientService.setSelection(event.getSelection()); //Set patient 
				statusLineManager.setMessage(event.getSelection().toString());
			}
		});
		newViewer.setContentProvider(new PatientContentProvider());
		newViewer.setLabelProvider(new PatientLabelProvider());
		newViewer.setInput(this);
		
		return newViewer;
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
