package de.lmu.ifi.dbs.medmon.sensor.ui.wizard;

import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;
import de.lmu.ifi.dbs.medmon.sensor.jobs.ImportJob;
import de.lmu.ifi.dbs.medmon.sensor.ui.wizard.pages.ImportDataPage;
import de.lmu.ifi.dbs.medmon.sensor.ui.wizard.pages.SourcePage;

public class ImportWizard extends Wizard {

	private SourcePage sourcePage;
	private ImportDataPage dataPage;

	private ISensor<?> sensor;
	private Patient patient;

	public ImportWizard() {
		setWindowTitle("Datenimport");
	}

	public ImportWizard(ISensor<?> sensor, Patient patient) {
		this();
		this.sensor = sensor;
		this.patient = patient;
	}

	@Override
	public void addPages() {
		if (patient == null && sensor == null) {
			sourcePage = new SourcePage();
			addPage(sourcePage);
		}

		dataPage = new ImportDataPage();
		addPage(dataPage);
	}

	@Override
	public boolean performFinish() {
		Patient patient = sourcePage.getPatient();
		Date start = dataPage.getStart();
		Date end = dataPage.getEnd();
		System.out.println("Patient: " + patient);
		System.out.println("Start: " + start);
		System.out.println("End: " + end);
		persistData();
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page.getPreviousPage() == sourcePage)
			sourcePage.importData();
		if (page == dataPage)
			dataPage.setViewerInput(sourcePage.getData());
		return super.getNextPage(page);
	}

	private void persistData() {
		if (patient == null)
			patient = sourcePage.getPatient();
		ISensorDataContainer[] container = dataPage.getSelection();
		for (ISensorDataContainer c : container) {
			System.out.println("Check ContainerType: " + c + " Type: " + c.getType() + " Hour: "
					+ ISensorDataContainer.HOUR);
			if (c.getType() == ISensorDataContainer.HOUR)
				persist(c, patient.getId());

		}
	}

	private void persist(ISensorDataContainer container, int id) {
		System.out.println("Trying to persist: " + container + " for id " + id);
		EntityManager em = JPAUtil.currentEntityManager();

		em.getTransaction().begin();
		try {
			System.out.println("Beginning Transaction for Container " + container);
			if (sensor == null)
				sensor = sourcePage.getSensor();
			IConverter converter = sensor.getConverter();
			Object[] sensorData;
			sensorData = container.getSensorData(converter);
			for (Object each : sensorData) {
				Data data = (Data) each;
				data.getId().setPatientId(id);
				em.persist(data);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Committing....");
		em.getTransaction().commit();
		System.out.println("Commited Transaction");
	}

}
