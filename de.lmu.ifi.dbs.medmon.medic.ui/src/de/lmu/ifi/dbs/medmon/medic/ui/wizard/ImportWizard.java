package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.jobs.persistence.PersistJob;
import de.lmu.ifi.dbs.medmon.medic.core.util.ApplicationConfigurationUtil;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectDataPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SensorPage;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.sensor.core.container.BlockDescriptor;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class ImportWizard extends Wizard {

	private static final String String = null;
	private SensorPage sourcePage;
	private SelectDataPage dataPage;

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
			sourcePage = new SensorPage();
			addPage(sourcePage);
		}

		dataPage = new SelectDataPage();
		addPage(dataPage);
	}

	@Override
	public boolean performFinish() {
		if (sensor == null)
			sensor = sourcePage.getSensor();
		if (patient == null)
			patient = sourcePage.getPatient();
		IConverter converter = sensor.getConverter();
		ISensorDataContainer[] children = dataPage.getSelection();
		RootSensorDataContainer root = new RootSensorDataContainer(patient.toString(),children);
		//Set the global selection
		Activator.getPatientService().setSelection(root, IPatientService.SENSOR_CONTAINER);
		//Persist
		if(dataPage.isPersist()) {
			if(children.length == 0) {
				MessageDialog.openError(getShell(), "Keine Daten", "Sie muessen Daten auswaehlen");
				return false;
			}
				
			ISensorDataContainer c = children[0];
			String file = (String) c.getBlock().getDescriptor().getAttribute(BlockDescriptor.FILE);
			file = moveSensorFile(file, patient);
			new PersistJob("Daten in Datenbank speichern", file, root, sensor, patient).schedule();
		}
			
		return true;
	}
	
	private String moveSensorFile(String oldFilePath, Patient patient) {		
		String sep = System.getProperty("file.separator");
		String returns = ApplicationConfigurationUtil.getPatientFolder(patient);
		returns += "data" + sep;
		String name = oldFilePath.substring(oldFilePath.lastIndexOf(sep) + 1);
		//DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh:mm");
		returns += df.format(new Date()) + "-" + name;
		
		File oldFile = new File(oldFilePath);
		File newFile = new File(returns);
		try {
			if(newFile.createNewFile())
				if(oldFile.renameTo(newFile))
					oldFile.delete();	
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		return returns;
		
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {		
		if (page == dataPage)
			dataPage.setViewerInput(sourcePage.importData());
		return super.getNextPage(page);
	}
	


}
