package de.lmu.ifi.dbs.medmon.sensor.dataImport.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class ImportWizard extends Wizard {

	private ImportPage1Patient patientPage;
	private ImportPage2Source sourcePage;
	private ImportPage3Data dataPage;
	//private AnalyzePage analyzePage;
	
	public ImportWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		patientPage = new ImportPage1Patient();
		sourcePage = new ImportPage2Source();
		dataPage = new ImportPage3Data();
		addPage(patientPage);
		addPage(sourcePage);
		addPage(dataPage);
	}
		
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page instanceof ImportPage3Data) 
			sourcePage.importData();
		return super.getNextPage(page);
	}
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
