package de.sendsor.accelerationSensor.model;

import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorData;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorDataClass;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorDataTimestamp;

@SensorDataClass(dimension = 3)
public class Data {

	@SensorDataTimestamp private long date;
	
	@SensorData	private int x;
	@SensorData private int y;
	@SensorData private int z;
	
	public Data(long date, int x, int y, int z) {
		this.date = date;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
	
}
