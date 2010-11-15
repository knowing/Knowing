package de.lmu.ifi.dbs.medmon.sensor.ui.provider;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;

public class PatientProposalProvider implements IContentProposalProvider {

	
	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		EntityManager entityManager = JPAUtil.currentEntityManager();
		Query query = entityManager.createNamedQuery("Patient.likeName");
		query.setParameter("firstname", contents.toLowerCase() + "%");
		query.setParameter("lastname", contents.toLowerCase() + "%");
		List<Patient>results = query.getResultList();
		
		int index = 0;
		IContentProposal[] returns = new IContentProposal[results.size()];
		for (Patient patient : results) {
			String content = patient.toString();
			returns[index++] = new ContentProposal(content);
		}
		return returns;
	}

}
