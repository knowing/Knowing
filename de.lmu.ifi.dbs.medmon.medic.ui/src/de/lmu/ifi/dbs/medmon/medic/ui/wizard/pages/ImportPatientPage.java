package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;

public class ImportPatientPage extends WizardPage {
	private Text text;

	/**
	 * Create the wizard.
	 */
	public ImportPatientPage() {
		super("patientImportPage");
		setTitle("Patient Import");
		setDescription("Waehlen Sie eine Datei aus um einen Patient zu importieren");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button bFile = new Button(container, SWT.NONE);
		bFile.setText("Datei auswaehlen");
		
		Group gImport = new Group(container, SWT.NONE);
		gImport.setText("Import");
		gImport.setLayout(new GridLayout(1, false));
		gImport.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Button bCluster = new Button(gImport, SWT.CHECK);
		bCluster.setText("Vergleichsdaten");
		
		Button bSensorData = new Button(gImport, SWT.CHECK);
		bSensorData.setText("Sensordaten");
		new Label(container, SWT.NONE);
	}

}
