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
@NamedQueries({
    @NamedQuery(name = "Patient.findAll", query = "SELECT p FROM Patient p"),
    @NamedQuery(name = "Patient.findById", query = "SELECT p FROM Patient p WHERE p.id = :id"),
    @NamedQuery(name = "Patient.findByFirstname", query = "SELECT p FROM Patient p WHERE p.firstname = :firstname"),
    @NamedQuery(name = "Patient.findByLastname", query = "SELECT p FROM Patient p WHERE p.lastname = :lastname"),
    @NamedQuery(name = "Patient.findByInsuranceId", query = "SELECT p FROM Patient p WHERE p.insuranceId = :insuranceId"),
    @NamedQuery(name = "Patient.findIdentical", query = "SELECT p FROM Patient p WHERE p.firstname = :firstname AND p.lastname = :lastname AND p.birth = :birth"),
    @NamedQuery(name = "Patient.likeName", query="SELECT p FROM Patient p WHERE lower(p.lastname) LIKE :lastname OR lower(p.firstname) LIKE :firstname")})
public class Patient implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final short MALE = 0;
	public static final short FEMALE = 1;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable=false)
	private int id;

	@Column(length=30)
	private String firstname;

	@Column(length=30)
	private String lastname;
	
	@Column
	private short gender;
	
	@Column(unique = true)
	private String insuranceId;
	
    @Temporal( TemporalType.DATE)
	private Date birth;

    @Lob()
	private String comment;

    @Temporal( TemporalType.DATE)
	private Date therapystart;

	//bi-directional many-to-one association to Appointment
	@OneToMany(mappedBy="patient")
	private Set<Appointment> appointments;

	//bi-directional many-to-one association to Data
	@OneToMany(mappedBy="patient")
	private Set<Data> data;
	
	//bi-directional many-to-one association to Data
	@OneToMany(mappedBy="patient")
	private Set<Archiv> archives;

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

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getInsuranceId() {
		return insuranceId;
	}
	
	public void setInsuranceId(String insuranceId) {
		this.insuranceId = insuranceId;
	}

	public short getGender() {
		return this.gender;
	}

	public void setGender(short gender) {
		this.gender = gender;
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
	
	public Set<Archiv> getArchives() {
		return archives;
	}
	
	public void setArchives(Set<Archiv> archives) {
		this.archives = archives;
	}
	
	@Override
	public String toString() {
		return firstname + " " + lastname;
	}
	
}