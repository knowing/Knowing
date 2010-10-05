package de.lmu.ifi.dbs.medmon.sensor.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.algorithm.wizards.pages.AnalyzePage;
import de.lmu.ifi.dbs.medmon.sensor.wizard.pages.PatientPage;
import de.lmu.ifi.dbs.medmon.sensor.wizard.pages.DataSourcePage;

/**
 * Wizard to quickly analyze a patient. Asks for:
 * <li>patient
 * <li>data source
 * <li>algorithm
 * 
 * @author muki
 * @version 0.3
 */
public class ImportExternalWizard extends Wizard {

	private PatientPage patientPage;
	private DataSourcePage sourcePage;
	//private ImportPage3Data dataPage;
	private AnalyzePage analyzePage;
	
	public ImportExternalWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		patientPage = new PatientPage();
		sourcePage = new DataSourcePage();
		analyzePage = new  AnalyzePage();
		addPage(patientPage);
		addPage(sourcePage);
		addPage(analyzePage);
		//dataPage = new ImportPage3Data();
		//addPage(dataPage);
	}
			
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page instanceof DataSourcePage) 
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
