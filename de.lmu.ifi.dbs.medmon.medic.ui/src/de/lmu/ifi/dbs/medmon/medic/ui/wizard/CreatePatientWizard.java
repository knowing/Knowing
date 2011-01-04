package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.CreatePatientPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectDataPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectMPUPage;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SensorPage;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class CreatePatientWizard extends Wizard implements IWorkbenchWizard, IExecutableExtension {

	/* Pages */
	private CreatePatientPage patientpage;
		
	private String finalPerspectiveId;

	public CreatePatientWizard() {
		setWindowTitle("Patient erstellen");		
	}

	@Override
	public void addPages() {
		addPage(patientpage = new CreatePatientPage());
	}
	

	@Override
	public boolean performFinish() {
		Patient patient = patientpage.getPatient();
		savePatient(patient);
		if(finalPerspectiveId != null && !finalPerspectiveId.isEmpty()) {
			try {
				PlatformUI.getWorkbench().showPerspective(finalPerspectiveId, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			} catch (WorkbenchException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	private void savePatient(Patient patient) {
		EntityManager entityManager = JPAUtil.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(patient);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		finalPerspectiveId = config.getAttribute("finalPerspective"); //$NON-NLS-1$		
	}

}
