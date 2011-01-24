package de.lmu.ifi.dbs.medmon.sensor.core.util;

import de.lmu.ifi.dbs.medmon.database.model.Sensor;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class SensorAdapter {

	private String name;
	private String version;
	private String type;

	private boolean available;
	private String tooltip;
	
	private ISensor sensorExtension;
	private Sensor sensorEntity;
	
	public SensorAdapter(ISensor sensorExtension, Sensor sensorEntity) {
		this.sensorExtension = sensorExtension;
		this.sensorEntity = sensorEntity;
		this.name = sensorExtension.getName();
		this.version = sensorExtension.getVersion();
		this.type = String.valueOf(sensorExtension.getType());
		
		this.available = true;
	}
	
	public SensorAdapter(ISensor sensorExtension) {
		this.sensorExtension = sensorExtension;
		this.name = sensorExtension.getName();
		this.version = sensorExtension.getVersion();
		this.type = String.valueOf(sensorExtension.getType());
		
		this.available = false;
	}
	
	public SensorAdapter(Sensor sensorEntity) {
		this.sensorEntity = sensorEntity;
		this.name = sensorEntity.getName();
		this.version = sensorEntity.getVersion();
		this.type = String.valueOf(sensorEntity.getType());
		
		this.available = false;
	}

	public boolean isAvailable() {
		return available;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getType() {
		return type;
	}

	public String getDefaultPath() {
		return sensorEntity.getDefaultpath();
	}
	
	public void setDefaultPath(String defaultpath) {
		sensorEntity.setDefaultpath(defaultpath);
	}
	
	protected ISensor getSensorExtension() {
		return sensorExtension;
	}
	
	protected void setSensorExtension(ISensor sensorExtenion) {
		this.sensorExtension = sensorExtenion;
	}

	protected Sensor getSensorEntity() {
		return sensorEntity;
	}
	
	protected void setSensorEntity(Sensor sensorEntity) {
		this.sensorEntity = sensorEntity;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SensorAdapter [name=");
		builder.append(name);
		builder.append(", version=");
		builder.append(version);
		builder.append(", type=");
		builder.append(type);
		builder.append(", defaultPath=");
		builder.append(sensorEntity.getDefaultpath());
		builder.append(", available=");
		builder.append(available);
		builder.append(", sensorExtension=");
		builder.append(sensorExtension);
		builder.append(", sensorEntity=");
		builder.append(sensorEntity);
		builder.append("]");
		return builder.toString();
	}
	
	

}
