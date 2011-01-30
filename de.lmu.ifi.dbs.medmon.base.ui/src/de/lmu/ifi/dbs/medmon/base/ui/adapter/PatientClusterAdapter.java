package de.lmu.ifi.dbs.medmon.base.ui.adapter;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;

public class PatientClusterAdapter {

	private final Patient patient;
	private final ClusterUnit unit;
	
	public PatientClusterAdapter(Patient patient, ClusterUnit unit) {
		this.patient = patient;
		this.unit = unit;
	}
	

	public Patient getPatient() {
		return patient;
	}
	
	public ClusterUnit getCluster() {
		return unit;
	}

	public String getName() {
		return unit.getName();
	}

	public String getDescription() {
		return unit.getDescription();
	}
	
	public boolean isDefault() {
		if(patient.getCluster() == null)
			return false;
		return unit.getName().equals(patient.getCluster());
	}
	
}
