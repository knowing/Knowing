package de.lmu.ifi.dbs.medmon.sensor.core.jobs;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;

public class PersistJob extends Job {

	private static final PersistRule rule = new PersistRule();
	
	private final Data[] data;

	public PersistJob(String name, Data[] data) {
		super(name);
		this.data = data;
		setRule(rule);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("persist", IProgressMonitor.UNKNOWN);
		EntityManager entityManager = JPAUtil.currentEntityManager();
		entityManager.getTransaction().begin();

		for (Data each : data)
			entityManager.persist(each);
		
		entityManager.getTransaction().commit();
		monitor.done();
		
		return Status.OK_STATUS;
	}
	
	

}
