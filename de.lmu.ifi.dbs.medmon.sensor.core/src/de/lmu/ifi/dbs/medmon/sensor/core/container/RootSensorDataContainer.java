package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.util.Date;

public class RootSensorDataContainer<E> extends AbstractSensorDataContainer<E> {

	public RootSensorDataContainer() {
		super(ContainerType.ROOT, null);
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

	@Override
	public Date getTimestamp() {
		return null;
	}

}
