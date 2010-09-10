package de.lmu.ifi.dbs.medmon.patient.sampledata;

import java.util.Date;
import java.util.Set;

public class Patient {

	private int id;
	private String firstname;
	private String lastname;
	private Date birth;
	private int gender;
	private Set<SensorData> sensorData;
	
	public Patient() {
		// TODO Auto-generated constructor stub
	}
	
	public Patient(String firstname, String lastname) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}



	@Override
	public String toString() {
		return firstname + " " + lastname;
	}

	public Set<SensorData> getSensorData() {
		return sensorData;
	}

	public void setSensorData(Set<SensorData> sensorData) {
		this.sensorData = sensorData;
	}
	
	
}
