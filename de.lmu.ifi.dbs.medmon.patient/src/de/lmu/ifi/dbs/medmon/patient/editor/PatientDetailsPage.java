package de.lmu.ifi.dbs.medmon.patient.editor;

import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.model.SensorData;
import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;
import de.lmu.ifi.dbs.medmon.sensor.provider.SensorContentProvider;
import de.lmu.ifi.dbs.medmon.sensor.provider.SensorLabelProvider;
import de.lmu.ifi.dbs.medmon.sensor.viewer.SensorTableViewer;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.beans.PojoObservables;

public class PatientDetailsPage implements IDetailsPage {
	
	private IManagedForm managedForm;
	
	private DataBindingContext bindingContext;
	
	private Patient patient;	
	private Text firstname, lastname;
	private GridData gd_firstname, gd_lastname;
	private SensorTableViewer viewer;
	
	private boolean dirty;
	
	public PatientDetailsPage() {
	}
	

	@Override
	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDirty() {
		return dirty;
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
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh() {
		System.out.println("PatientDetailsPage Refresh");

	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		System.out.println("SelectionChanged: " + selection);
		commit(true);
		if(!selection.isEmpty() && selection instanceof IStructuredSelection) {
			if(bindingContext != null) bindingContext.dispose();
			patient = (Patient)((IStructuredSelection)selection).getFirstElement();
			bindingContext = initDataBindings();
			//TODO reset viewer input
		}
	}


	@Override
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();	
		parent.setLayout(new ColumnLayout());
		DirtyController dirtyController = new DirtyController();

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
		firstname.addListener(SWT.Modify, dirtyController);
		
		toolkit.createLabel(gClient, "Nachname");
		lastname = toolkit.createText(gClient, "", SWT.BORDER);
		gd_lastname = new GridData(150, SWT.DEFAULT);
		lastname.setLayoutData(gd_lastname);
		lastname.addListener(SWT.Modify, dirtyController);
		
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
		
		Button analyse = toolkit.createButton(sClient, "analysieren", SWT.PUSH);
		analyse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Button delete  = toolkit.createButton(sClient, "entfernen", SWT.PUSH);
		delete.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Button iimport = toolkit.createButton(sClient, "importieren", SWT.PUSH);
		iimport.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		toolkit.paintBordersFor(sClient);
		sSection.setClient(sClient);
		
		viewer = new SensorTableViewer(table);
		Set<SensorData> set = SampleDataFactory.getSensorData();
		viewer.setInput(set.toArray(new SensorData[set.size()]));	
		
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue firstnameObserveTextObserveWidget = SWTObservables.observeText(firstname, SWT.Modify);
		IObservableValue patientFirstnameObserveValue = PojoObservables.observeValue(patient, "firstname");
		bindingContext.bindValue(firstnameObserveTextObserveWidget, patientFirstnameObserveValue, null, null);
		//
		IObservableValue lastnameObserveTextObserveWidget = SWTObservables.observeText(lastname, SWT.Modify);
		IObservableValue patientLastnameObserveValue = PojoObservables.observeValue(patient, "lastname");
		bindingContext.bindValue(lastnameObserveTextObserveWidget, patientLastnameObserveValue, null, null);
		//
		return bindingContext;
	}
	
	private class DirtyController implements Listener {

		@Override
		public void handleEvent(Event event) {
			dirty = true;	
		}		
	}
}
