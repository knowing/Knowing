package de.lmu.ifi.dbs.medmon.developer.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.lmu.ifi.dbs.medmon.developer.ui.wizard.pages.ClusterWizardPage;

public class ClusterWizard extends Wizard implements IWorkbenchWizard{

	private ClusterWizardPage page;
	
	@Override
	public void addPages() {
		page = new ClusterWizardPage();
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}

}
