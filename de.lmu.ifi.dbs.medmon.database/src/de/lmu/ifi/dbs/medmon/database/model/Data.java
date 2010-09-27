package de.lmu.ifi.dbs.medmon.database.model;

import java.sql.Timestamp;
import java.util.Date;

public class Data {

	private int x;
	private int y;
	private int z;
	private Timestamp timestamp;
	
	private boolean analyzed;
	private Date analyzedDate;
	
	public Data() {
		timestamp = new Timestamp(0);
	}
		
	public Data(int x, int y, int z, Timestamp timestamp) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.timestamp = timestamp;
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

	public void setTimestamp(Timestamp timestamp) {
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
	
	@Override
	public String toString() {
		return "SensorData [x=" + x + ", y=" + y + ", z=" + z + ", timestamp="
				+ timestamp + "]";
	}

	
}
