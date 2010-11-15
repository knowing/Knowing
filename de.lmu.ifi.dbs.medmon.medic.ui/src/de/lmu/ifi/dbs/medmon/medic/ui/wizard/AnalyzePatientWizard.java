package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SummaryPage;

public class AnalyzePatientWizard extends Wizard {

	private SummaryPage page1;
	//private ImportDataPage page2;
	//private AnalyzePage page3;
	
	@Override
	public boolean performFinish() {
		System.out.println("Open Visualize Perspective");
		return true;
	}
	
	@Override
	public void addPages() {
		page1 = new SummaryPage();
		//page2 = new ImportDataPage();
		//page3 = new AnalyzePage();
		addPage(page1);
		//addPage(page2);
		//addPage(page3);		
	}

}
