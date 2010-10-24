package model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

/**
 * The primary key class for the DATA database table.
 * 
 */
@Embeddable
public class DataPK implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name = "PATIENT_ID", nullable = false)
	private int patientId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date record;

	public DataPK() {}
	
	public DataPK(int patientId, Date record) {
		this.patientId = patientId;
		this.record = record;
	}


	public int getPatientId() {
		return this.patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public Date getRecord() {
		return record;
	}

	public void setRecord(Date record) {
		this.record = record;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DataPK)) {
			return false;
		}
		DataPK castOther = (DataPK) other;
		return (this.patientId == castOther.patientId)
				&& this.record.equals(castOther.record);

	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.patientId;
		hash = hash * prime + this.record.hashCode();

		return hash;
	}

	@Override
	public String toString() {
		return "DataPK [patientId=" + patientId + ", record=" + record.getTime() + "]";
	}
	
	
	
}