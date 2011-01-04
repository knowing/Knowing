package de.lmu.ifi.dbs.medmon.jobs.persistence;

import java.awt.Container;
import java.io.IOException;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;

public class PersistJob extends Job {

	private static final PersistRule rule = new PersistRule();

	private Data[] data;
	private final int patientID;

	private ISensorDataContainer root;

	private IConverter converter;

	public PersistJob(String name, Data[] data, int patientID) {
		super(name);
		this.data = data;
		this.patientID = patientID;
		setRule(rule);
		setUser(true);
	}

	public PersistJob(String name, ISensorDataContainer root, IConverter converter, int patientID) {
		super(name);
		this.root = root;
		this.patientID = patientID;
		this.converter = converter;
		setRule(rule);
		setUser(true);
	}

	protected IStatus singlePersist(Data[] data, IProgressMonitor monitor) {
		monitor.beginTask("persist", IProgressMonitor.UNKNOWN);
		EntityManager entityManager = JPAUtil.createEntityManager();
		entityManager.getTransaction().begin();

		for (Data each : data) {
			each.getId().setPatientId(patientID);
			entityManager.persist(each);
		}

		entityManager.getTransaction().commit();
		entityManager.close();
		monitor.done();
		return Status.OK_STATUS;
	}

	protected IStatus multiPersist(ISensorDataContainer root, IConverter converter, IProgressMonitor monitor) {
		EntityManager entityManager = JPAUtil.createEntityManager();
		monitor.beginTask("Persist: " + root.getName(), root.getChildren().length);
		for (ISensorDataContainer c : root.getChildren()) {
			if (!c.getType().equals(ContainerType.HOUR)) {
				multiPersist(c, converter, monitor);
			} else {
				try {
					monitor.subTask(c.getName());
					entityManager.getTransaction().begin();
					Object[] sensorData = c.getSensorData(converter);
					for (Object each : sensorData) {
						Data data = (Data) each;
						data.getId().setPatientId(patientID);
						entityManager.persist(data);
					}
					entityManager.getTransaction().commit();
					sensorData = null;
					if (monitor.isCanceled())
						break;
					monitor.worked(1);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		entityManager.close();
		monitor.done();
		return Status.OK_STATUS;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (data != null)
			return singlePersist(data, monitor);

		if (root != null && converter != null)
			return multiPersist(root, converter, monitor);

		return Status.OK_STATUS;
	}

}
