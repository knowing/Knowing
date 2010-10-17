package de.lmu.ifi.dbs.medmon.sensor.data;

import de.lmu.ifi.dbs.medmon.database.model.Data;

public class BlockSensorDataContainer extends AbstractSensorDataContainer {

	
	private final String file;
	private final int blockstart;

	public BlockSensorDataContainer(ISensorDataContainer parent, String file, int blockstart) {
		super(parent, ISensorDataContainer.BLOCK);
		this.file = file;
		this.blockstart = blockstart;
	}
	
	public BlockSensorDataContainer(String file, int blockstart) {
		this(null, file, blockstart);
	}
	
	@Override
	public Data[] getSensorData() {
		
		return super.getSensorData();
	}

	@Override
	public String getName() {
		return "Block: " + blockstart;
	}

}
