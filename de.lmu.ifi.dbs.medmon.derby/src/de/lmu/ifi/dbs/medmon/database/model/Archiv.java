package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the COMMENTS database table.
 * 
 */
@Entity
@Table(name = "ARCHIV")
@NamedQueries({ @NamedQuery(name = "Archiv.findByPatient", query = "SELECT a FROM Archiv a WHERE a.patient.id = :patientId") })
public class Archiv implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private int id;

	// bi-directional many-to-one association to Patient
	@ManyToOne
	@JoinColumn(name = "PATIENT_ID", nullable = false)
	private Patient patient;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date timestamp;

	@Column(nullable = false)
	private String title;

	@Lob
	@Column(nullable = false)
	private String comments;

	@Column
	private String path;

	// bi-directional many-to-one association to Data
	@OneToMany(mappedBy = "archiv")
	private Set<Data> data;

	public Archiv() {
		timestamp = new Date();
	}

	public Archiv(Patient patient, String title, String comments, String path) {
		this();
		this.patient = patient;
		this.title = title;
		this.comments = comments;
		this.path = path;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	protected void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Set<Data> getData() {
		return this.data;
	}

	public void setData(Set<Data> data) {
		this.data = data;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

}