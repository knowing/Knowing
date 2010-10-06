package de.lmu.ifi.dbs.medmon.sensor.wizard;

import java.util.Date;

import javax.persistence.EntityManager;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.sensor.data.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.wizard.pages.ImportDataPage;
import de.lmu.ifi.dbs.medmon.sensor.wizard.pages.SourcePage;

public class ImportWizard extends Wizard {

	private SourcePage sourcePage;
	private ImportDataPage dataPage;
	
	public ImportWizard() {
		setWindowTitle("New Wizard");
	}

	@Override
	public void addPages() {
		sourcePage = new SourcePage();
		dataPage = new ImportDataPage();
		addPage(sourcePage);
		addPage(dataPage);
	}

	@Override
	public boolean performFinish() {
		Patient patient = sourcePage.getPatient();
		ISensorDataContainer data = sourcePage.getData();
		Date start = dataPage.getStart();
		Date end = dataPage.getEnd();
		System.out.println("Patient: " + patient);
		System.out.println("Data: " + data);
		System.out.println("Start: " + start);
		System.out.println("End: " + end);
		persistData();
		return true;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page.getPreviousPage() == sourcePage)
			sourcePage.importData();
		return super.getNextPage(page);
	}
	
	private void persistData() {
		Patient patient = sourcePage.getPatient();
		Data[] data = sourcePage.getData().getSensorData();
		EntityManager em = JPAUtil.currentEntityManager();

		em.getTransaction().begin();
		for(Data each : data) {
			each.getId().setPatientId(patient.getId());
			em.persist(each);
		}
		em.getTransaction().commit();
	}

}
