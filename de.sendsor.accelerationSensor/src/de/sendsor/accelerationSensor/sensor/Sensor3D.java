package de.sendsor.accelerationSensor.sensor;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class Sensor3D implements ISensor<Data> {

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getName() {
		return "3D Master Sensor";
	}

	@Override
	public int getType() {
		return ISensor.MASTER;
	}

	@Override
	public Data[] getData() {
		return null;
	}

}
