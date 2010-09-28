package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the DATA database table.
 * 
 */
@Entity
@Table(name="DATA")
public class Data implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DataPK id;

	private Timestamp timestamp;
	
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
  
    

	public Data(int x, int y, int z, Timestamp imported) {
		super();
		this.timestamp = imported;
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
	
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
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

	
}