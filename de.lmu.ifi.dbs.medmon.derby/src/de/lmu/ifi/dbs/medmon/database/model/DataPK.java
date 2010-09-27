package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the DATA database table.
 * 
 */
@Embeddable
public class DataPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="PATIENT_ID", unique=true, nullable=false)
	private int patientId;

    @Temporal( TemporalType.DATE)
	@Column(unique=true, nullable=false)
	private java.util.Date record;

    public DataPK() {
    }
	public int getPatientId() {
		return this.patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public java.util.Date getRecord() {
		return this.record;
	}
	public void setRecord(java.util.Date record) {
		this.record = record;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DataPK)) {
			return false;
		}
		DataPK castOther = (DataPK)other;
		return 
			(this.patientId == castOther.patientId)
			&& this.record.equals(castOther.record);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.patientId;
		hash = hash * prime + this.record.hashCode();
		
		return hash;
    }
}