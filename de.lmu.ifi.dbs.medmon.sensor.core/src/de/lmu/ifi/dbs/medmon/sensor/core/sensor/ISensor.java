package de.lmu.ifi.dbs.medmon.sensor.core.sensor;

import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;

/**
 * Representing a generic sensor
 * 
 * @author Nepomuk Seiler
 * @version 0.6
 */
public interface ISensor<E> {

	public static final int MASTER = 1;
	public static final int SLAVE = 2;
	
	public String getVersion();
	
	public String getName();
	
	public int getType();
	
	public E[] getData();
	
	public IConverter<E> getConverter();
	
}
