package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.jobs.persistence.PersistJob;
import de.lmu.ifi.dbs.medmon.jobs.persistence.PersistRule;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.ImportDataPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SourcePage;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

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
		if (sensor == null)
			sensor = sourcePage.getSensor();
		if (patient == null)
			patient = sourcePage.getPatient();
		IConverter converter = sensor.getConverter();
		ISensorDataContainer[] children = dataPage.getSelection();
		RootSensorDataContainer root = new RootSensorDataContainer(children);
		new PersistJob(root.getName(), root, converter, patient.getId()).schedule();
	}

	private void persist(ISensorDataContainer container, int id) {
		System.out.println("Trying to persist: " + container + " for id " + id);
		if (sensor == null)
			sensor = sourcePage.getSensor();
		IConverter converter = sensor.getConverter();
		Object[] sensorData;
		try {
			sensorData = container.getSensorData(converter);
			PersistJob job = new PersistJob(container.getName(), (Data[])sensorData, id);
			job.setUser(true);
			job.setRule(new PersistRule());
			job.schedule();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		
	}

}
