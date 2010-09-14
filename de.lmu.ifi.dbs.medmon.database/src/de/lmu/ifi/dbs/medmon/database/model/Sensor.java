package de.lmu.ifi.dbs.medmon.database.model;

import java.util.Set;

public class Sensor {

	private int id;
	private String version;
	private Patient patient;
	private Set<SensorData> data;
	
	public Sensor() {}
	
	public Sensor(String version) {
		this.version = version;
	}

	public Sensor(Patient patient, Set<SensorData> data) {
		this.patient = patient;
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Set<SensorData> getData() {
		return data;
	}

	public void setData(Set<SensorData> data) {
		this.data = data;
	}
	
	
	
}
