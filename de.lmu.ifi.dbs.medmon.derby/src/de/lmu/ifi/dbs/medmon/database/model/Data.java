package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Calendar;
import java.util.Date;


/**
 * The persistent class for the DATA database table.
 * 
 */
@Entity
@Table(name="DATA")
@NamedQueries({
    @NamedQuery(name = "Data.findAll", query = "SELECT d FROM Data d ORDER BY d.id.record" )})
public class Data implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DataPK id;

	@Column(name="imported", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date imported;
	
	@Column(nullable=false)
	private int x;

	@Column(nullable=false)
	private int y;

	@Column(nullable=false)
	private int z;

	//bi-directional many-to-one association to Comment
    @ManyToOne
	@JoinColumn(name="COMMENT_ID")
	private Comment comment;

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
	
	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return this.z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public Comment getComment() {
		return this.comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
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
				+ ", y=" + y + ", z=" + z + ", comment=" + comment
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