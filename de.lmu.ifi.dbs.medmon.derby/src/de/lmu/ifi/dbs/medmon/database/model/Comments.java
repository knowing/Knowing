/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 *
 * @author Muki
 */
@Entity
@Table(name = "COMMENTS")
@NamedQueries({
    @NamedQuery(name = "Comments.findAll", query = "SELECT c FROM Comments c"),
    @NamedQuery(name = "Comments.findByPatientId", query = "SELECT c FROM Comments c WHERE c.patientId = :patientId"),
    @NamedQuery(name = "Comments.findBySensorId", query = "SELECT c FROM Comments c WHERE c.sensorId = :sensorId"),
    @NamedQuery(name = "Comments.findByDate", query = "SELECT c FROM Comments c WHERE c.date = :date")})
public class Comments implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "PATIENT_ID")
    private Integer patientId;
    @Basic(optional = false)
    @Column(name = "SENSOR_ID")
    private int sensorId;
    @Basic(optional = false)
    @Lob
    @Column(name = "COMMENTS")
    private String comments;
    @Column(name = "DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @JoinColumn(name = "PATIENT_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    private Patient patient;

    public Comments() {
    }

    public Comments(Integer patientId) {
        this.patientId = patientId;
    }

    public Comments(Integer patientId, int sensorId, String comments) {
        this.patientId = patientId;
        this.sensorId = sensorId;
        this.comments = comments;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (patientId != null ? patientId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Comments)) {
            return false;
        }
        Comments other = (Comments) object;
        if ((this.patientId == null && other.patientId != null) || (this.patientId != null && !this.patientId.equals(other.patientId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "demodb.Comments[patientId=" + patientId + "]";
    }

}
