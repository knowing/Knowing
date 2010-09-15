package de.lmu.ifi.dbs.medmon.sensor.dataImport.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ImportPage3Data extends WizardPage{

	private Composite container;
	
	protected ImportPage3Data() {
		super("Daten auswaehlen");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		
		setControl(container);
	}

}
