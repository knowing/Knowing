package de.lmu.ifi.dbs.medmon.sensor.dataImport.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Scale;

public class ImportPage3Data extends WizardPage{

	private Composite container;
	
	protected ImportPage3Data() {
		super("Daten auswaehlen");
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		
		setControl(container);
		//setPageComplete(false);
	}

}
