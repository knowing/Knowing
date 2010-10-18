package de.lmu.ifi.dbs.medmon.sensor.data;

public class BlockSensorDataContainer extends AbstractSensorDataContainer {

	public BlockSensorDataContainer(ISensorDataContainer parent, String file, int blockstart) {
		super(parent, ISensorDataContainer.BLOCK, new Block(file, blockstart, blockstart+512));
	}
	
	public BlockSensorDataContainer(String file, int blockstart) {
		this(null, file, blockstart);
	}
	
	@Override
	public String getName() {
		return block.toString();
	}

}
