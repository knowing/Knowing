/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Muki
 */
@Entity
@Table(name = "DATA")
@NamedQueries({
    @NamedQuery(name = "Data.findAll", query = "SELECT d FROM Data d"),
    @NamedQuery(name = "Data.findByPatientId", query = "SELECT d FROM Data d WHERE d.dataPK.patientId = :patientId"),
    @NamedQuery(name = "Data.findBySensorId", query = "SELECT d FROM Data d WHERE d.dataPK.sensorId = :sensorId"),
    @NamedQuery(name = "Data.findByDate", query = "SELECT d FROM Data d WHERE d.date = :date")})
public class Data implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DataPK dataPK;
    @Column(name = "DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Basic(optional = false)
    @Lob
    @Column(name = "SENSORDATA")
    private String sensordata;
    @JoinColumn(name = "SENSOR_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Sensor sensor;
    @JoinColumn(name = "PATIENT_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Patient patient;

    public Data() {
    }

    public Data(DataPK dataPK) {
        this.dataPK = dataPK;
    }

    public Data(DataPK dataPK, String sensordata) {
        this.dataPK = dataPK;
        this.sensordata = sensordata;
    }

    public Data(int patientId, int sensorId) {
        this.dataPK = new DataPK(patientId, sensorId);
    }

    public DataPK getDataPK() {
        return dataPK;
    }

    public void setDataPK(DataPK dataPK) {
        this.dataPK = dataPK;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSensordata() {
        return sensordata;
    }

    public void setSensordata(String sensordata) {
        this.sensordata = sensordata;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
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
        hash += (dataPK != null ? dataPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Data)) {
            return false;
        }
        Data other = (Data) object;
        if ((this.dataPK == null && other.dataPK != null) || (this.dataPK != null && !this.dataPK.equals(other.dataPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "demodb.Data[dataPK=" + dataPK + "]";
    }

}
