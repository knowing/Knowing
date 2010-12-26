package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.ImportPatientPage;

public class ImportPatientWizard extends Wizard implements IImportWizard {

	public ImportPatientWizard() {
		setWindowTitle("New Wizard");
	}

	@Override
	public void addPages() {
		addPage(new ImportPatientPage());
	}

	@Override
	public boolean performFinish() {
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
				
	}

}
