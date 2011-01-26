package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.model.Sensor;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectDBDataPage;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

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
		EntityManager em = JPAUtil.createEntityManager();
		Patient patient = page.getPatient();
		// Select all days imported
		List<Data> results = em.createNamedQuery("Data.findByPatient", Data.class)
				.setParameter("patient", patient).getResultList();
		em.close();
		if (results.isEmpty()) {
			page.setErrorMessage("Keine Daten vom Patienten vorhanden");
			return true;
		}

		Sensor dbsensor = results.get(0).getSensor();
		List<ISensor<?>> sensors = FrameworkUtil.<ISensor<?>>evaluateExtensionsAsList(ISensor.SENSOR_ID);
		ISensor<?> sensor = null;
		for (Object o : sensors) {
			ISensor<?> s = (ISensor<?>) o;
			String id = Sensor.parseId(s.getName(), s.getVersion());
			if (id.equals(dbsensor.getId())) {
				sensor = s;
				break;
			}
		}
		if (sensor == null) {
			page.setErrorMessage("Der Sensor " + dbsensor.getName() + " ist nicht installiert");
			return false;
		}

		// The new input for the Viewer
		RootSensorDataContainer<Object> root = new RootSensorDataContainer<Object>(patient.toString());
		IConverter converter = sensor.getConverter();
		for (Data data : results) {
			try {
				ISensorDataContainer c = converter.convertToContainer(data.getFile(), ContainerType.WEEK, ContainerType.HOUR, null);
				if(c instanceof RootSensorDataContainer) 
					((RootSensorDataContainer)c).setName(data.getFile());
				root.addChild(c);
			} catch (IOException e) {
				e.printStackTrace();
				Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage());
				ErrorDialog errorDialog = new ErrorDialog(getShell(), "Datei fehlerhaft", "Datei " + data.getFile()
						+ " konnte nicht geoeffnet werden", status, IStatus.ERROR);
				errorDialog.open();
				continue;
				
			}
		}

		Activator.getPatientService().setSelection(root, IPatientService.SENSOR_CONTAINER);
		return true;
	}

}
