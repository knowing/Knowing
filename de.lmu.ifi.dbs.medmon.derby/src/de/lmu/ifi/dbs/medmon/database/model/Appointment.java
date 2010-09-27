package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the APPOINTMENT database table.
 * 
 */
@Entity
@Table(name="APPOINTMENT")
public class Appointment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id;

    @Temporal( TemporalType.DATE)
	private Date date;

	@Column(length=30)
	private String name;

	//bi-directional many-to-one association to Patient
    @ManyToOne
	@JoinColumn(name="PATIENT_ID", nullable=false)
	private Patient patient;

    public Appointment() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Patient getPatient() {
		return this.patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
}