package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.internal.Processor;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectDataPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectDPUPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SensorPage;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class QuickAnalyseWizard extends Wizard implements INewWizard, IExecutableExtension {

	private SensorPage sourcePage = new SensorPage();
	private SelectDataPage dataPage = new SelectDataPage();
	private SelectDPUPage mpuPage;
	
	/* to prevent getNextPage setting input twice */
	private boolean firstcall = true;

	private void initSelections(IPatientService service) {
		/* Can skip sensorPage */
		Patient patient = (Patient) service.getSelection(IPatientService.PATIENT);
		ISensor sensor = (ISensor) service.getSelection(IPatientService.SENSOR);
		if (patient != null && sensor != null) 
			sourcePage = new SensorPage(patient, sensor);
		
		/* Can skip dataPage */
		ISensorDataContainer c = (ISensorDataContainer) service.getSelection(IPatientService.SENSOR_CONTAINER);
		if (c != null) 
			dataPage = new SelectDataPage(new ISensorDataContainer[] { c });
		
	}

	@Override
	public void addPages() {
		IPatientService service = Activator.getPatientService();
		initSelections(service);
		addPage(sourcePage);
		addPage(dataPage);
		addPage(mpuPage = new SelectDPUPage());
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == dataPage) {
			if(firstcall) 
				dataPage.setViewerInput(sourcePage.importData());
			firstcall = !firstcall;
		}
		return super.getNextPage(page);
	}

	@Override
	public boolean performFinish() {
		DataProcessingUnit dpu = (DataProcessingUnit) Activator.getPatientService().getSelection(IPatientService.DPU);
		if(dpu == null)
			return false;
		Processor processor = Processor.getInstance();
		Map<String, IAnalyzedData> acc = null;
		Patient patient = sourcePage.getPatient();
		ISensor sensor = sourcePage.getSensor();
		ISensorDataContainer[] selection = dataPage.getSelection();
		for (ISensorDataContainer c : selection) {
			//new ImportJob(c.getBlock(), sensor.getConverter()).schedule();
			try {
				Object[] input = sensor.getConverter().readData(c);
				acc = processor.run(dpu, input, acc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

}
