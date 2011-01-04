package de.lmu.ifi.dbs.medmon.medic.ui.pages;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.ISharedImages;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.CommandUtil;

public class PatientDetailsPage implements IDetailsPage {

	private IManagedForm managedForm;

	// Databinding: Patient <-> UI Elements
	private DataBindingContext bindingContext;

	// For databinding purpose
	private GridData gd_firstname, gd_lastname;

	// UI Elements
	private Patient patient;
	private Text firstname, lastname;
	private Button analyse;
	private TableViewer viewer;

	private boolean dirty; // Represents dirty state

	private Label labelId;

	public PatientDetailsPage() {
	}

	@Override
	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		// commit(true);
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			if (bindingContext != null)
				bindingContext.dispose();
			patient = (Patient) ((IStructuredSelection) selection)
					.getFirstElement();
			bindingContext = initDataBindings();
			// TODO reset viewer input
		}
	}

	@Override
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		ColumnLayout parent_layout = new ColumnLayout();
		parent_layout.maxNumColumns = 2;
		parent.setLayout(parent_layout);
		Controller controller = new Controller();

		/* Patient Information */
		Section gSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		gSection.setText("Patienteninformationen");

		Composite gClient = toolkit.createComposite(gSection, SWT.WRAP);
		GridLayout gLayout = new GridLayout(2, false);
		gLayout.marginWidth = 5;
		gLayout.marginHeight = 5;
		gClient.setLayout(gLayout);
		
		Label lID = new Label(gClient, SWT.NONE);
		toolkit.adapt(lID, true, true);
		lID.setText("ID");
		
		labelId = toolkit.createLabel(gClient, "");

		toolkit.createLabel(gClient, "Name");
		firstname = toolkit.createText(gClient, "", SWT.BORDER);
		gd_firstname = new GridData(150, SWT.DEFAULT);
		firstname.setLayoutData(gd_firstname);
		firstname.addListener(SWT.Modify, controller);

		toolkit.createLabel(gClient, "Nachname");
		lastname = toolkit.createText(gClient, "", SWT.BORDER);
		gd_lastname = new GridData(150, SWT.DEFAULT);
		lastname.setLayoutData(gd_lastname);
		lastname.addListener(SWT.Modify, controller);

		gSection.setClient(gClient);
		
		Label lSeprator = toolkit.createLabel(gClient, "");
		GridData gd_label = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_label.heightHint = 20;
		lSeprator.setLayoutData(gd_label);

		
		Label lblGeboren = new Label(gClient, SWT.NONE);
		toolkit.adapt(lblGeboren, true, true);
		lblGeboren.setText("Geboren");
		new Label(gClient, SWT.NONE);
		
		Label lblGeschlecht = new Label(gClient, SWT.NONE);
		toolkit.adapt(lblGeschlecht, true, true);
		lblGeschlecht.setText("Geschlecht");
		new Label(gClient, SWT.NONE);

		/* Buttons Section */
		Section bSection = toolkit.createSection(parent, Section.NO_TITLE);
		
		Composite bClient = toolkit.createComposite(bSection);
		bClient.setLayout(new FillLayout());
		ImageHyperlink sensorLink = toolkit.createImageHyperlink(bClient, SWT.NONE);
		sensorLink.setImage(Activator.getImageDescriptor(ISharedImages.IMG_GO_NEXT_48).createImage());
		sensorLink.setText("Sensordaten auswaehlen");
		
		sensorLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				CommandUtil.openView(IMedmonConstants.SENSOR_MANAGEMENT_VIEW);
			}
		});
		
		bSection.setClient(bClient);

		//hookActions();
	}	
	

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		// Patient.firstname
		IObservableValue firstnameObserveTextObserveWidget = SWTObservables.observeText(firstname, SWT.Modify);
		IObservableValue patientFirstnameObserveValue = PojoObservables.observeValue(patient, "firstname");
		bindingContext.bindValue(firstnameObserveTextObserveWidget,	patientFirstnameObserveValue, null, null);
		// Patient.lastname
		IObservableValue lastnameObserveTextObserveWidget = SWTObservables.observeText(lastname, SWT.Modify);
		IObservableValue patientLastnameObserveValue = PojoObservables.observeValue(patient, "lastname");
		bindingContext.bindValue(lastnameObserveTextObserveWidget,patientLastnameObserveValue, null, null);
		//
		IObservableValue labelObserveTextObserveWidget = SWTObservables.observeText(labelId);
		IObservableValue patientIdObserveValue = PojoObservables.observeValue(patient, "id");
		bindingContext.bindValue(labelObserveTextObserveWidget, patientIdObserveValue, null, null);
		//
		return bindingContext;
	}

	protected void hookActions() {
		analyse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//OpenDefaultPerspectiveHandler.excuteCommand();
			}
		});
		
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public boolean isDirty() {
		// return dirty;
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		System.out.println("PatientDetialsPage commit save: " + onSave);
	}

	@Override
	public boolean setFormInput(Object input) {
		System.out.println("SetFormInput: " + input);
		return false;
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
		System.out.println("PatientDetailsPage Refresh");
	}
	
	private class Controller implements Listener, ISelectionChangedListener {

		@Override
		public void handleEvent(Event event) {
			//TODO hook all buttons
			dirty = true;
		}

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			if(event.getSelection().isEmpty())
				analyse.setEnabled(false);
			else
				analyse.setEnabled(true);
		}
	}
}
