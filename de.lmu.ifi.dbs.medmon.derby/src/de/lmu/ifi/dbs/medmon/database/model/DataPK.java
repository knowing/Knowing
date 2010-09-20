/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Muki
 */
@Embeddable
public class DataPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "PATIENT_ID")
    private int patientId;
    @Basic(optional = false)
    @Column(name = "SENSOR_ID")
    private int sensorId;

    public DataPK() {
    }

    public DataPK(int patientId, int sensorId) {
        this.patientId = patientId;
        this.sensorId = sensorId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += patientId;
        hash += sensorId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataPK)) {
            return false;
        }
        DataPK other = (DataPK) object;
        if (this.patientId != other.patientId) {
            return false;
        }
        if (this.sensorId != other.sensorId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "demodb.DataPK[patientId=" + patientId + ", sensorId=" + sensorId + "]";
    }

}
