package de.lmu.ifi.dbs.medmon.sensor.core.sensor;

import java.io.File;
import java.io.IOException;

import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;

/**
 * Representing a generic sensor
 * 
 * @author Nepomuk Seiler
 * @version 0.7
 */
public interface ISensor<E> {

	public static final String SENSOR_ID = "de.lmu.ifi.dbs.medmon.sensor";
	public static final int MASTER = 1;
	public static final int SLAVE = 2;
	
	public String getVersion();
	
	public String getName();
	
	public String getDescription();
	
	public int getType();
	
	public ISensorDataContainer<E> getData(String path) throws IOException;
	
	public IConverter<E> getConverter();
	
	public boolean isSensor(File dir);
	
}
