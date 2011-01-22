package de.lmu.ifi.dbs.medmon.jobs.persistence;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.model.Sensor;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.BlockDescriptor;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class PersistJob extends Job {

	private static final PersistRule rule = new PersistRule();
	
	private final Patient patient;
	private ISensorDataContainer root;
	private final ISensor sensor;
	private final String filepath;


	public PersistJob(String name, String filepath, ISensorDataContainer root, ISensor sensor, Patient patient) {
		super(name);
		this.root = root;
		this.sensor = sensor;
		this.patient = patient;
		this.filepath = filepath;
		setRule(rule);
		setUser(true);
	}
	
	protected IStatus persist(ISensorDataContainer root, ISensor sensor, IProgressMonitor monitor) {
		monitor.beginTask("persist", IProgressMonitor.UNKNOWN);
		Block block = root.getBlock();
		BlockDescriptor descriptor = block.getDescriptor();
		EntityManager entityManager = JPAUtil.createEntityManager();
		
		entityManager.getTransaction().begin();
		Data entity = new Data();
		entity.setFrom(descriptor.getStartDate());
		entity.setTo(descriptor.getEndDate());
		entity.setPatient(patient);
		
		Sensor dbSensor = entityManager.find(Sensor.class, Sensor.parseId(sensor.getName(), sensor.getVersion()));
		if(dbSensor == null)
			return new Status(IStatus.ERROR, "de.lmu.ifi.dbs.medmon.jobs.persistence", "Sensor not in DB");
		entity.setSensor(dbSensor);
		
		String file = (String) descriptor.getAttribute(BlockDescriptor.FILE);
		String name = file.substring(file.lastIndexOf(System.getProperty("file.separator")) + 1);
		entity.setOriginalFile(name);
		
		//
		entity.setFile(filepath);
		
		entityManager.persist(entity);
		entityManager.getTransaction().commit();
		entityManager.close();
		monitor.done();
		return Status.OK_STATUS;
	}


	@Override
	protected IStatus run(IProgressMonitor monitor) {
		return persist(root, sensor, monitor);
	}

}
