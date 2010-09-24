package de.lmu.ifi.dbs.medmon.patient.editor;

import javax.management.openmbean.OpenDataException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;

import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.algorithm.provider.AlgorithmContentProvider;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;
import de.lmu.ifi.dbs.medmon.patient.Activator;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.sensor.viewer.SensorTableViewer;
import de.lmu.ifi.dbs.medmon.visualizer.handler.OpenDefaultPerspectiveHandler;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.beans.PojoObservables;

public class PatientDetailsPage implements IDetailsPage {

	private IManagedForm managedForm;

	// Databinding: Patient <-> UI Elements
	private DataBindingContext bindingContext;

	// For databinding purpose
	private GridData gd_firstname, gd_lastname;

	// UI Elements
	private Patient patient;
	private Text firstname, lastname;
	private Button analyse, delete, iimport;
	private SensorTableViewer viewer;

	private boolean dirty; // Represents dirty state

	public PatientDetailsPage() {
	}

	@Override
	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
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
		parent.setLayout(new ColumnLayout());
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

		/* Sensor DB */

		Section sSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE);
		sSection.setText("Sensordaten");
		sSection.setDescription("Bereits importierte Sensordaten");

		Composite sClient = toolkit.createComposite(sSection);
		GridLayout sLayout = new GridLayout(3, false);
		sClient.setLayout(sLayout);

		Table table = new Table(sClient, SWT.NULL);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 3;
		table.setLayoutData(data);

		analyse = toolkit.createButton(sClient, "analysieren", SWT.PUSH);
		analyse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		analyse.setEnabled(false);
		delete = toolkit.createButton(sClient, "entfernen", SWT.PUSH);
		delete.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		iimport = toolkit.createButton(sClient, "importieren", SWT.PUSH);
		iimport.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		toolkit.paintBordersFor(sClient);
		sSection.setClient(sClient);

		viewer = new SensorTableViewer(table);
		viewer.addSelectionChangedListener(controller);
		viewer.setInput(SampleDataFactory.getSensorDataArray());
		hookActions();
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
		return bindingContext;
	}

	protected void hookActions() {
		analyse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getPatientService().setSelection(viewer.getSelection()); //Set SensorData
				setDefaultAlgorithm();
				OpenDefaultPerspectiveHandler.excuteCommand();
			}
		});
		
	}
	
	//TODO Create Configuration for standard algorithm
	/**
	 * Uebergangsmethode bis per Konfiguration ein Standardalgorithmus
	 * gesetzt werden kann!!
	 */
	private void setDefaultAlgorithm() {
		ISensorDataAlgorithm[] algorithms = AlgorithmContentProvider.getAlgorithms();
		if(algorithms != null && algorithms[0] != null)
			Activator.getPatientService().setSelection(algorithms[0], IPatientService.ALGORITHM);
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
