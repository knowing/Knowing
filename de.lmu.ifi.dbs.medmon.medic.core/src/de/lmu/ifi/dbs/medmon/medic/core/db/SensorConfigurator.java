package de.lmu.ifi.dbs.medmon.medic.core.db;

import java.util.List;

import javax.persistence.EntityManager;

import de.lmu.ifi.dbs.medmon.database.model.Sensor;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class SensorConfigurator {

	
	public static void initializeSensorDB() {
		List<ISensor<?>> sensors = FrameworkUtil.<ISensor<?>>evaluateExtensionsAsList(ISensor.SENSOR_ID);
		EntityManager em = JPAUtil.createEntityManager();
		em.getTransaction().begin();
		for (ISensor<?> sensor : sensors) {
			String id = sensor.getName() + ":" + sensor.getVersion();
			Sensor dbsensor = em.find(Sensor.class , id );
			if(dbsensor == null) {
				em.persist(new Sensor(sensor.getName(), sensor.getVersion(), sensor.getType()));
			}
		}
		em.getTransaction().commit();
		em.close();
	}
	
	//TODO Maybe put the sensors to a preference page
}
