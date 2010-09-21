package de.lmu.ifi.dbs.medmon.patient.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import com.swtdesigner.ResourceManager;


public class SummaryPage extends WizardPage {
	
	private Text tPatient;
	private Text tAlgorithm;
	
	private String OK_IMAGE = "icons/24/gtk-apply.png";
	private String MISSING_IMAGE = "icons/24/gtk-close.png";
	private String RES_PLUGIN = "de.lmu.ifi.dbs.medmon.rcp";

	public SummaryPage() {
		super("Zusammenfassung");
		setMessage("Patientendaten analyisieren");
		setTitle("Patienten Wizard");
		setDescription("");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout gl_container = new GridLayout(3, false);
		gl_container.horizontalSpacing = 15;
		gl_container.verticalSpacing = 15;
		gl_container.marginTop = 5;
		gl_container.marginRight = 5;
		gl_container.marginBottom = 5;
		container.setLayout(gl_container);

		/* Patient */
		Label lPatient = new Label(container, SWT.NONE);
		lPatient.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,false, 1, 1));
		lPatient.setText("Patient ");

		tPatient = new Text(container, SWT.READ_ONLY);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1,1);
		gd_text.widthHint = 150;
		tPatient.setLayoutData(gd_text);

		Label iPatient = new Label(container, SWT.NONE);
		iPatient.setImage(ResourceManager.getPluginImage(RES_PLUGIN, OK_IMAGE));
		iPatient.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,1, 1));
		
		/* Data */
		Label lData = new Label(container, SWT.NONE);
		lData.setText("Sensordaten");
		
		new Label(container, SWT.NONE); //Placeholder
		
		Label iData = new Label(container, SWT.NONE);
		iData.setImage(ResourceManager.getPluginImage(RES_PLUGIN, MISSING_IMAGE));
		
		
		/* Algorithm */
		Label lAlgorithm = new Label(container, SWT.NONE);
		lAlgorithm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lAlgorithm.setText("Algorithmus");
				
		tAlgorithm = new Text(container, SWT.NONE);
		tAlgorithm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label iAlgorithm = new Label(container, SWT.NONE);
		iAlgorithm.setImage(ResourceManager.getPluginImage(RES_PLUGIN, MISSING_IMAGE));
		
		//Add the container as control
		setControl(container);
	}

}
