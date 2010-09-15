package de.lmu.ifi.dbs.medmon.sensor.dataImport.wizard;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;

public class ImportPage1Patient extends WizardPage {

	//User can choose next, but not finish
	private boolean flip = true;
	
	private Patient patient;

	private Composite container;

	private Button rCurrent; //Radio Current Patient
	private Button rSelect; //Radio Select Patient
	private Button select; //Select special Patient
	private PatientPageController controller;
	
	protected ImportPage1Patient() {
		super("Patient auswaehlen");
		initCurrentPatient();
		setDescription("Bitte waehlen sie einen Patienten aus");
		setTitle("Patient auswaehlen");
	}
	
	private void initCurrentPatient() {
		//TODO Get real DefaultPatient
		patient = SampleDataFactory.getData()[0];
		setMessage(patient.toString());
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return flip;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		controller = new PatientPageController();
		
		rCurrent = new Button(container, SWT.RADIO);
		rCurrent.addSelectionListener(controller);
		rCurrent.setSelection(true);
		new Label(container, SWT.NONE).setText("Aktueller Patient");
		
		rSelect  = new Button(container, SWT.RADIO);
		rSelect.addSelectionListener(controller);
			
		select 	= new Button(container, SWT.PUSH);
		select.setText("Patient auswaehlen");
		select.setEnabled(false);
		select.addSelectionListener(controller);

		setControl(container);
		setPageComplete(false);
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	private class PatientPageController extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(e.getSource() == rCurrent ) {
				initCurrentPatient();
				select.setEnabled(false);
				//Updates the navigation buttons
				flip = true;
				getContainer().updateButtons();
			} else if(e.getSource() == rSelect) {
				patient = null;
				flip = false;
				select.setEnabled(true);
				getContainer().updateButtons();
				//setErrorMessage("Bitte einen Patienten auswaehlen");
			} else if(e.getSource() == select) {
				ElementListSelectionDialog dialog = new ElementListSelectionDialog(container.getShell(), new LabelProvider());
				dialog.setBlockOnOpen(true);
				dialog.setTitle("Patient auswaehlen");
				dialog.setElements(SampleDataFactory.getData());
				if(dialog.open() == Window.OK) {
					//Assuming that there's only one Patient Selection
					patient = (Patient) dialog.getResult()[0];
					flip = true;
					getContainer().updateButtons();
					setMessage(patient.toString());
				}
			}
		}
	}
}
