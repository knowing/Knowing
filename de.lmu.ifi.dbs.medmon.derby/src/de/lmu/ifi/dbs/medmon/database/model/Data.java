package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorData;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorDataClass;


/**
 * The persistent class for the DATA database table.
 * 
 */
@SensorDataClass(dimension = 3)
@Entity
@Table(name="DATA")
@NamedQueries({
    @NamedQuery(name = "Data.findAll", query = "SELECT d FROM Data d ORDER BY d.id.record" )})
public class Data implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DataPK id;

	//@Column(name="imported", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable=false)
	@Basic(optional = false)
	@Column(name = "imported", insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date imported;
	
	@SensorData
	@Column(nullable=false)
	private double x;

	@SensorData
	@Column(nullable=false)
	private double y;

	@SensorData
	@Column(nullable=false)
	private double z;

	//bi-directional many-to-one association to Comment
    @ManyToOne
    @JoinColumn(name="ARCHIV_ID", updatable=false, insertable=false)
	private Archiv archiv;

	//bi-directional many-to-one association to Patient
    @ManyToOne
	@JoinColumn(name="PATIENT_ID", nullable=false, insertable=false, updatable=false)
	private Patient patient;

    public Data() {  }
  
	public Data(DataPK id, int x, int y, int z) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public DataPK getId() {
		return this.id;
	}

	public void setId(DataPK id) {
		this.id = id;
	}
	
	public Date getImported() {
		return imported;
	}

	public void setImported(Date imported) {
		this.imported = imported;
	}
	
	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return this.z;
	}

	public void setZ(double z) {
		this.z = z;
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
	
	@Override
	public String toString() {
		return "Data [id=" + id + ", imported=" + imported + ", x=" + x
				+ ", y=" + y + ", z=" + z + ", comment=" + archiv
				+ ", patient=" + patient + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Data)) {
			return false;
		}
		Data other = (Data) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}