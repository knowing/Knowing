package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.util.Date;

public class RootSensorDataContainer<E> extends AbstractSensorDataContainer<E> {

	private String name;

	public RootSensorDataContainer() {
		this("root");
	}
	
	public RootSensorDataContainer(String name) {
		super(ContainerType.ROOT, null);
		this.name = name;
	}
	
	public RootSensorDataContainer(String name, ISensorDataContainer<E>[] children) {
		this(name);
		for(ISensorDataContainer<E> child : children) 
			addChild(child);
		initBlock();
	}
	
	private void initBlock() {
		this.block = null;
		for (ISensorDataContainer c : getChildren()) {
			if(block == null) {
				block = c.getBlock();
				continue;
			}
			block = block.merge(c.getBlock());
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Date getTimestamp() {
		return null;
	}

}
