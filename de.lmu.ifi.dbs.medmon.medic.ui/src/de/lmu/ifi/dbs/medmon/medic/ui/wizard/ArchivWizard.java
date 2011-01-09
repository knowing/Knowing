package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.lmu.ifi.dbs.medmon.database.model.Archiv;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.CreateArchivPage;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;

public class ArchivWizard extends Wizard implements IWorkbenchWizard, IExecutableExtension {

	public ArchivWizard() {
		setWindowTitle("Patienteneintrag Assistent");
	}

	@Override
	public void addPages() {
		Patient patient = (Patient) Activator.getPatientService().getSelection(IPatientService.PATIENT);
		addPage(new CreateArchivPage(patient));
	}

	@Override
	public boolean performFinish() {
		CreateArchivPage page = (CreateArchivPage) getPage(CreateArchivPage.PAGE_NAME);
		String title = page.getTitle();
		String comment = page.getComment();
		Patient patient = page.getPatient();
		EntityManager em = JPAUtil.createEntityManager();
		em.getTransaction().begin();
		Archiv archiv = new Archiv(patient,title, comment, null);
		em.persist(archiv);
		em.getTransaction().commit();
		return true;
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
				
	}
	
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
				
	}

}
