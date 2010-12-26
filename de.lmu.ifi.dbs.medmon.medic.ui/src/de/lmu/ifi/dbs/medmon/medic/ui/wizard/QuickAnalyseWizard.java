package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectMPUPage;

public class QuickAnalyseWizard extends Wizard implements INewWizard {

	private SelectMPUPage page3;
	
	public QuickAnalyseWizard() {
		
	}
	
	@Override
	public void addPages() {
		addPage(page3 = new SelectMPUPage());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
