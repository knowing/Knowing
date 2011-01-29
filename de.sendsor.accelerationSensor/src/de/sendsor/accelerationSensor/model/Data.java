package de.sendsor.accelerationSensor.model;

import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorData;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorDataClass;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorDataTimestamp;

@SensorDataClass(dimension = 3)
public class Data {

	@SensorDataTimestamp private long date;
	
	@SensorData	private double x;
	@SensorData private double y;
	@SensorData private double z;
	
	public Data(long date, double x, double y, double z) {
		this.date = date;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
	
}
