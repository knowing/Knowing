package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectDataSourcePage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectMPUPage;

public class TrainClusterWizard extends Wizard implements IWorkbenchWizard{

	private SelectDataSourcePage page1;
	private SelectMPUPage page2;

	public TrainClusterWizard() {
		setWindowTitle("Vergleichsdaten erzeugen");
	}

	@Override
	public void addPages() {
		addPage(page1 = new SelectDataSourcePage());
		addPage(page2 = new SelectMPUPage());
	}

	@Override
	public boolean performFinish() {
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
				
	}

}
