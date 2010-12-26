package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.ExportPatientPage;

public class ExportPatientWizard extends Wizard implements IExportWizard {

	public ExportPatientWizard() {
		setWindowTitle("New Wizard");
	}

	@Override
	public void addPages() {
		addPage(new ExportPatientPage());
	}

	@Override
	public boolean performFinish() {
		return false;
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
				
	}

}
