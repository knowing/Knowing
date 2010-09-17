package de.lmu.ifi.dbs.medmon.sensor.dataImport.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.algorithm.wizards.pages.AnalyzePage;

public class ImportWizard extends Wizard {

	private ImportPage1Patient patientPage;
	private ImportPage2Source sourcePage;
	//private ImportPage3Data dataPage;
	private AnalyzePage analyzePage;
	
	public ImportWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		patientPage = new ImportPage1Patient();
		sourcePage = new ImportPage2Source();
		analyzePage = new  AnalyzePage();
		addPage(patientPage);
		addPage(sourcePage);
		addPage(analyzePage);
		//dataPage = new ImportPage3Data();
		//addPage(dataPage);
	}
			
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page instanceof ImportPage2Source) 
			sourcePage.importData();
		return super.getNextPage(page);
	}
	
	@Override
	public boolean performFinish() {
		System.out.println("Perform finish");
		System.out.println("Patient: " + patientPage.getPatient());
		System.out.println("SensorData : " + sourcePage.getSensorData());
		System.out.println("Algorithm: " + analyzePage.getAlgorithm());
		//TODO Open VisualizePerspective
		//TODO Set View Inputs
		return true;
	}

}
