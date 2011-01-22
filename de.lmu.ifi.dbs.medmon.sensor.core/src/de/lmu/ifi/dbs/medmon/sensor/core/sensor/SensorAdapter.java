package de.lmu.ifi.dbs.medmon.sensor.core.sensor;

import javax.persistence.EntityManager;

import de.lmu.ifi.dbs.medmon.database.model.Sensor;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;

public class SensorAdapter {

	private String name;
	private String version;
	private String type;
	private String defaultPath;

	private boolean available;
	
	private ISensor sensor;
	private Sensor dbsensor;
	
	private SensorAdapter(ISensor sensor, Sensor dbsensor) {
		this.sensor = sensor;
		this.dbsensor = dbsensor;
		this.name = sensor.getName();
		this.version = sensor.getVersion();
		this.type = String.valueOf(sensor.getType());
		this.defaultPath = dbsensor.getDefaultpath();
		
		this.available = true;
	}
	
	private SensorAdapter(ISensor sensor) {
		this.name = sensor.getName();
		this.version = sensor.getVersion();
		this.type = String.valueOf(sensor.getType());
		this.defaultPath = null;
		
		this.available = false;
	}
	
	private SensorAdapter(Sensor dbsensor) {
		this.name = dbsensor.getName();
		this.version = dbsensor.getVersion();
		this.type = String.valueOf(dbsensor.getType());
		this.defaultPath = dbsensor.getDefaultpath();
		
		this.available = false;
	}

	public static SensorAdapter getInstance(ISensor sensor) {
		String name = sensor.getName();
		String version = sensor.getVersion();
		
		//Search for corresponding Sensor in DB
		EntityManager em = JPAUtil.createEntityManager();
		String id = Sensor.parseId(name, version);
		Sensor dbsensor = em.find(Sensor.class, id);
		//Couldn't find one
		if (dbsensor == null)
			return new SensorAdapter(sensor);
		
		return new SensorAdapter(sensor, dbsensor);
	}

	public static SensorAdapter getInstance(Sensor sensor) {
		
		return null;
	}

	public boolean isAvailable() {
		return available;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getType() {
		return type;
	}

	public String getDefaultPath() {
		return defaultPath;
	}
	
	public ISensor getSensor() {
		return sensor;
	}
	
	public Sensor getDbsensor() {
		return dbsensor;
	}

}
