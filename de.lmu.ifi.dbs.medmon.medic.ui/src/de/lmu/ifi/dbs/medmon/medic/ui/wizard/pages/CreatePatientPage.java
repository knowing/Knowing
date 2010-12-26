package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import org.eclipse.jface.viewers.ArrayContentProvider;
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
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;

public class CreatePatientPage extends WizardPage {

	//User can choose next, but not finish
	private boolean flip = true;
	
	private Patient patient;

	private Composite container;
	private Text tFirstname;
	private Text tLastname;
	private Text tSocialnumber;
		
	public CreatePatientPage() {
		super("Patient auswaehlen");
		setDescription("Bitte waehlen sie einen Patienten aus");
		setTitle("Patient auswaehlen");
	}
		
	@Override
	public boolean canFlipToNextPage() {
		return flip;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(4, false));

		setControl(container);
		
		Label lFirstname = new Label(container, SWT.NONE);
		lFirstname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lFirstname.setText("Vorname");
		
		tFirstname = new Text(container, SWT.BORDER);
		GridData gd_tFirstname = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_tFirstname.widthHint = 150;
		tFirstname.setLayoutData(gd_tFirstname);
		
		Label lLastname = new Label(container, SWT.NONE);
		lLastname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lLastname.setText("Nachname");
		
		tLastname = new Text(container, SWT.BORDER);
		GridData gd_tLastname = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_tLastname.widthHint = 150;
		tLastname.setLayoutData(gd_tLastname);
		
		Label lBirth = new Label(container, SWT.NONE);
		lBirth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lBirth.setText("Geburtsdatum");
		
		CDateTime dBirth = new CDateTime(container, CDT.BORDER | CDT.SPINNER);
		
		Label lGender = new Label(container, SWT.NONE);
		lGender.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lGender.setText("Geschlecht");
		
		ComboViewer comboViewer = new ComboViewer(container, SWT.NONE);
		Combo cBirth = comboViewer.getCombo();
		GridData gd_cBirth = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_cBirth.widthHint = 160;
		cBirth.setLayoutData(gd_cBirth);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setInput(new String[] {"Maennlich", "Weiblich" });
		
		Label lSocialnumber = new Label(container, SWT.NONE);
		lSocialnumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lSocialnumber.setText("Versicherungsnummer");
		
		tSocialnumber = new Text(container, SWT.BORDER);
		tSocialnumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		setPageComplete(true);
	}
	
	private void done() {
		flip = true;
		setPageComplete(true);
		if(patient != null)
			setMessage(patient.toString());
		else
			setMessage("Kein Patient ausgewaehlt");
	}
	
	public Patient getPatient() {
		return patient;
	}
}
