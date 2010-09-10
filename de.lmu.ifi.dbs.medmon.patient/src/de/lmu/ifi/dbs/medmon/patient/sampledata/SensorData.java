package de.lmu.ifi.dbs.medmon.patient.sampledata;

import java.util.Date;

public class SensorData {

	private int x;
	private int y;
	private int z;
	private Date timestamp;
	
	public SensorData() {
		timestamp = new Date();
	}

	public SensorData(int x, int y, int z) {
		super();
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "SensorData [x=" + x + ", y=" + y + ", z=" + z + ", timestamp="
				+ timestamp + "]";
	}
	
	
	
	
}
