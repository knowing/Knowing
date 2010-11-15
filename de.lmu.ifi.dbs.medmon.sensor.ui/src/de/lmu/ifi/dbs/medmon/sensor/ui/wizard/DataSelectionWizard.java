package de.lmu.ifi.dbs.medmon.sensor.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.sensor.ui.wizard.pages.DataSelectionPage;

public class DataSelectionWizard extends Wizard {

	private DataSelectionPage page;
	
	public DataSelectionWizard() {
		setWindowTitle("Data Selection Wizard");
	}

	@Override
	public void addPages() {
		page = new DataSelectionPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
