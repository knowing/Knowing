package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectDBDataPage;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.TimeSensorDataContainer;

public class DataSelectionWizard extends Wizard {

	private SelectDBDataPage page;

	public DataSelectionWizard() {
		setWindowTitle("Data Selection Wizard");
	}

	@Override
	public void addPages() {
		page = new SelectDBDataPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		EntityManager entityManager = JPAUtil.currentEntityManager();
		Patient patient = page.getPatient();
		//Select all days imported
		String query = "SELECT DISTINCT DATE( record ) FROM DATA WHERE patient_id = " + patient.getId();
		List<Date> resultList = entityManager.createNativeQuery(query).getResultList();
		//The new input for the Viewer
		RootSensorDataContainer<Object> root = new RootSensorDataContainer<Object>();
		Calendar calendar = GregorianCalendar.getInstance();
		//Check the days
		for (Date date : resultList) {
			Block block = new Block(entityManager, date.getTime(), date.getTime());
			TimeSensorDataContainer day = new TimeSensorDataContainer(ContainerType.DAY, block);
			Timestamp stamp = new Timestamp(date.getTime());
			//Getting the hours of the day
			String query2 = "SELECT DISTINCT HOUR( record ) FROM DATA WHERE patient_id = " + patient.getId() + " AND DATE(record) = '" + date + "'";
			List<Integer> resultList2 = entityManager.createNativeQuery(query2).getResultList();
			//Creating leafs
			for (Integer hour : resultList2) {
				calendar.setTime(date);
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				Date date2 = new Date(calendar.getTimeInMillis());
				Block block2 = new Block(entityManager, date2.getTime(), date2.getTime());
				day.addChild(new TimeSensorDataContainer(ContainerType.HOUR, block2));
			}
			root.addChild(day);
		}
		Activator.getPatientService().setSelection(root, IPatientService.SENSOR_CONTAINER);
		return true;
	}

}
