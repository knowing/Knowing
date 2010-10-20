package de.sendsor.accelerationSensor.sensor;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;
import de.sendsor.accelerationSensor.converter.SDRConverter;

public class Sensor3D implements ISensor<Data> {

	public static SDRConverter converter = new SDRConverter();
	
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
	
	@Override
	public IConverter<Data> getConverter() {
		return converter;
	}
	
}
