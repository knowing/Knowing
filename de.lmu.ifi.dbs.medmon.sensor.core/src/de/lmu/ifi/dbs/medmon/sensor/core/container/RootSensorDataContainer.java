package de.lmu.ifi.dbs.medmon.sensor.core.container;

public class RootSensorDataContainer extends AbstractSensorDataContainer {

	public RootSensorDataContainer() {
		super(ROOT, null);
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
