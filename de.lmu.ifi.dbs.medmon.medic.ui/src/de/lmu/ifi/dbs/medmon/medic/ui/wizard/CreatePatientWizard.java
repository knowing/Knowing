package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.CreatePatientPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectDataPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectMPUPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SensorPage;

public class CreatePatientWizard extends Wizard implements IWorkbenchWizard {

	public CreatePatientWizard() {
		setWindowTitle("Patient erstellen");
	}

	@Override
	public void addPages() {
		addPage(new CreatePatientPage());
		addPage(new SensorPage());
		addPage(new SelectDataPage());
		addPage(new SelectMPUPage());
	}

	@Override
	public boolean performFinish() {
		return false;
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
				
	}

}
