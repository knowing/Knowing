package de.lmu.ifi.dbs.medmon.sensor.sensors;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;
import de.lmu.ifi.dbs.medmon.sensor.data.ISensor;

/**
 * Filled up with samples
 * @author Nepomuk Seiler
 * @version 0.1
 */
public class Sensor3D implements ISensor<Data> {

	
	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getName() {
		return "3D Sensor";
	}

	@Override
	public int getType() {
		return MASTER;
	}

	@Override
	public Data[] getData() {
		return SampleDataFactory.getSensorDataArray();
	}

}
