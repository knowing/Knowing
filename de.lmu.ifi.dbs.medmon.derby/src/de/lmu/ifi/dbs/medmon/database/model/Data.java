package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the DATA database table.
 * 
 */
@Entity
@Table(name="DATA")
@NamedQueries({
    @NamedQuery(name = "Data.findAll", query = "SELECT d FROM Data d"),
    @NamedQuery(name = "Data.findByPatient", query = "SELECT d FROM Data d WHERE d.patient = :patient")})
public class Data implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "BEGIN_DATE", nullable=false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date from;
	
	@Column(name = "END_DATE", nullable=false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date to;
	
	@Column(name = "FILE")
	private String file;
	
	@Column
	private String originalFile;
		
	@ManyToOne
	@JoinColumn(name="SENSOR_ID", nullable=false)
	private Sensor sensor;

	//bi-directional many-to-one association to Comment
    @ManyToOne
    @JoinColumn(name="ARCHIV_ID")
	private Archiv archiv;

	//bi-directional many-to-one association to Patient
    //
    @ManyToOne
	@JoinColumn(name="PATIENT_ID", nullable=false, updatable=false)
	private Patient patient;

    public Data() {  }
  

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
		
	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}
	
	public String getFile() {
		return file;
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	
	public String getOriginalFile() {
		return originalFile;
	}
	
	public void setOriginalFile(String originalFile) {
		this.originalFile = originalFile;
	}
	
	public Archiv getArchiv() {
		return archiv;
	}
	
	public void setArchiv(Archiv archiv) {
		this.archiv = archiv;
	}
	
	public Patient getPatient() {
		return this.patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Sensor getSensor() {
		return sensor;
	}
	
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Data [id=");
		builder.append(id);
		builder.append(", from=");
		builder.append(from);
		builder.append(", to=");
		builder.append(to);
		builder.append(", sensor=");
		builder.append(sensor);
		builder.append(", archiv=");
		builder.append(archiv);
		builder.append(", patient=");
		builder.append(patient);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Data other = (Data) obj;
		if (id != other.id)
			return false;
		return true;
	}

}