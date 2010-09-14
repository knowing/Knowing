package de.lmu.ifi.dbs.medmon.database.model;

import java.util.Date;

public class SensorData {

	private int x;
	private int y;
	private int z;
	private Date timestamp;
	private Date recorded;
	
	private boolean analyzed;
	private Date analyzedDate;
	
	public SensorData() {
		timestamp = new Date();
	}
		
	public SensorData(int x, int y, int z, Date recorded) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.recorded = recorded;
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
	
	public boolean isAnalyzed() {
		return analyzed;
	}

	public void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}

	public Date getAnalyzedDate() {
		return analyzedDate;
	}

	public void setAnalyzedDate(Date analyzedDate) {
		this.analyzedDate = analyzedDate;
	}
	
	public Date getRecorded() {
		return recorded;
	}

	public void setRecorded(Date recorded) {
		this.recorded = recorded;
	}

	@Override
	public String toString() {
		return "SensorData [x=" + x + ", y=" + y + ", z=" + z + ", timestamp="
				+ timestamp + "]";
	}

	
}
