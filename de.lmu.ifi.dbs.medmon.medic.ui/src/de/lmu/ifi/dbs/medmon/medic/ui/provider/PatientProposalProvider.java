package de.lmu.ifi.dbs.medmon.medic.ui.provider;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;

public class PatientProposalProvider implements IContentProposalProvider {

	
	private final static char LEFT = '<';
	private final static char RIGHT = '>';

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		EntityManager em = JPAUtil.createEntityManager();
		Query query = em.createNamedQuery("Patient.likeName");
		query.setParameter("firstname", contents.toLowerCase() + "%");
		query.setParameter("lastname", contents.toLowerCase() + "%");
		List<Patient>results = query.getResultList();
		
		int index = 0;
		IContentProposal[] returns = new IContentProposal[results.size()];
		for (Patient patient : results) {
			String content = patient.toString() + "<" + patient.getId() + ">";
			returns[index++] = new ContentProposal(content);
		}
		em.close();
		return returns;
	}
	
	public static Patient parsePatient(String patient) {
		int left_index = patient.indexOf(LEFT);
		int right_index = patient.indexOf(RIGHT);
		if(right_index == -1 || left_index == -1) 
			return null;
		String idString = patient.substring(left_index+1, right_index);
		int id = Integer.valueOf(idString);
		EntityManager em = JPAUtil.createEntityManager();
		Patient returns = em.find(Patient.class, id);
		em.close();
		return returns;
	}
	
	public static String parseString(Patient patient) {
		return patient.toString() + "<" + patient.getId() + ">";
	}

}
