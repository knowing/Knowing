package de.lmu.ifi.dbs.medmon.sensor.data;

public class RootSensorDataContainer extends AbstractSensorDataContainer {

	public RootSensorDataContainer() {
		super(null, ROOT);
	}
	
	public RootSensorDataContainer(ISensorDataContainer[] children) {
		this();
		for(ISensorDataContainer child : children) 
			addChild(child);
	}

	@Override
	public String getName() {
		return "root";
	}

}
