package de.sendsor.accelerationSensor.sensor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;
import de.sendsor.accelerationSensor.converter.SDRConverter;
import de.sendsor.accelerationSensor.model.Data;

public class Sensor3D implements ISensor<Data> {

	private static final SDRConverter converter = new SDRConverter();
	
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
	public ISensorDataContainer<Data> getData(String path) throws IOException {
		//Sensor exists
		File sensorpath = new File(path);
		if(!sensorpath.exists() || !sensorpath.isDirectory())
			throw new IOException("Wrong path");
		
		//Get .sdr files
		File[] sdrFiles = sensorpath.listFiles(new FilenameFilter() {			
			@Override
			public boolean accept(File dir, String name) {		
				return name.endsWith(".sdr");
			}
		});
		
		//Sensor contains data
		if(sdrFiles.length < 1)
			throw new IOException("Sensor contains no data");
		
		//Convert it
		ISensorDataContainer<Data> root = new RootSensorDataContainer<Data>();
		for (File each : sdrFiles) {
			root.addChild(converter.convertToContainer(each.getAbsolutePath(), ContainerType.WEEK, ContainerType.HOUR, null));
		}
		
		return root;
	}
	
	@Override
	public boolean isSensor(File dir) {
		String[] files = dir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".sdr");
			}
		});
		return files.length > 0;
	}
	
	@Override
	public IConverter<Data> getConverter() {
		return converter;
	}

	@Override
	public String getDescription() {
		return "3D Bewegungssensor zum Aufzeichnen von Bewegungsdaten.";
	}
	
}
