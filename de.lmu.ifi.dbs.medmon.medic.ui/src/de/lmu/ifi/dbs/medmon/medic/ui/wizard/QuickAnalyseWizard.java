package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectDataPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectMPUPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SensorPage;

public class QuickAnalyseWizard extends Wizard implements INewWizard, IExecutableExtension {

	private SensorPage sourcePage;
	private SelectDataPage dataPage;
	private SelectMPUPage mpuPage;
	
	public QuickAnalyseWizard() {
		
	}
	
	@Override
	public void addPages() {
		addPage(sourcePage = new SensorPage());
		addPage(dataPage = new SelectDataPage());
		addPage(mpuPage = new SelectMPUPage());
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {			
		if (page == dataPage)
			dataPage.setViewerInput(sourcePage.importData());
		return super.getNextPage(page);
	}


	@Override
	public boolean performFinish() {
		return false;
	}
	
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {	
		
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}

}
