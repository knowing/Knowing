package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.Set;


/**
 * The persistent class for the PATIENT database table.
 * 
 */
@Entity
@Table(name="PATIENT")
public class Patient implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(unique=true, nullable=false)
	private int id;

    @Temporal( TemporalType.DATE)
	private Date birth;

    @Lob()
	private String comment;

	@Column(length=30)
	private String firstname;

	private short gender;

	@Column(length=30)
	private String lastname;

    @Temporal( TemporalType.DATE)
	private Date therapystart;

	//bi-directional many-to-one association to Appointment
	@OneToMany(mappedBy="patient")
	private Set<Appointment> appointments;

	//bi-directional many-to-one association to Data
	@OneToMany(mappedBy="patient")
	private Set<Data> data;

    public Patient() {
    }
    
    

	public Patient(String firstname, String lastname) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
	}



	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getBirth() {
		return this.birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public short getGender() {
		return this.gender;
	}

	public void setGender(short gender) {
		this.gender = gender;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Date getTherapystart() {
		return this.therapystart;
	}

	public void setTherapystart(Date therapystart) {
		this.therapystart = therapystart;
	}

	public Set<Appointment> getAppointments() {
		return this.appointments;
	}

	public void setAppointments(Set<Appointment> appointments) {
		this.appointments = appointments;
	}
	
	public Set<Data> getData() {
		return this.data;
	}

	public void setData(Set<Data> data) {
		this.data = data;
	}
	
}