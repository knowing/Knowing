package de.lmu.ifi.dbs.medmon.sensor.core.container;

public class RootSensorDataContainer<E> extends AbstractSensorDataContainer<E> {

	public RootSensorDataContainer() {
		super(ROOT, null);
	}
	
	public RootSensorDataContainer(ISensorDataContainer<E>[] children) {
		this();
		for(ISensorDataContainer<E> child : children) 
			addChild(child);
	}
	
	@Override
	public String getName() {
		return "root";
	}

}
