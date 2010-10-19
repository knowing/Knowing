package de.lmu.ifi.dbs.medmon.patient.ui.wizards;

import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.algorithm.ui.wizards.pages.AnalyzePage;
import de.lmu.ifi.dbs.medmon.sensor.ui.wizard.pages.ImportDataPage;

public class AnalyzePatientWizard extends Wizard {

	private SummaryPage page1;
	private ImportDataPage page2;
	private AnalyzePage page3;
	
	@Override
	public boolean performFinish() {
		System.out.println("Open Visualize Perspective");
		return true;
	}
	
	@Override
	public void addPages() {
		page1 = new SummaryPage();
		page2 = new ImportDataPage();
		page3 = new AnalyzePage();
		addPage(page1);
		addPage(page2);
		addPage(page3);		
	}

}
