package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class ExportPatientPage extends WizardPage {
	private Text text;
	private Text text_1;

	/**
	 * Create the wizard.
	 */
	public ExportPatientPage() {
		super("patientExportPage");
		setTitle("Patient Export");
		setDescription("Waehlen Sie einen Patienten und die Informationen zum Export aus");
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
		
		Button bPatient = new Button(container, SWT.NONE);
		bPatient.setText("Patient auswaehlen");
		
		Group gExport = new Group(container, SWT.BORDER);
		gExport.setText("Export");
		gExport.setLayout(new GridLayout(1, false));
		gExport.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Button bCluster = new Button(gExport, SWT.CHECK);
		bCluster.setText("Vergleichsdaten");
		
		Button bSensorData = new Button(gExport, SWT.CHECK);
		bSensorData.setText("Sensordaten");
		
		text_1 = new Text(container, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button bPlace = new Button(container, SWT.NONE);
		bPlace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		bPlace.setText("Speicherort");
	}

}
