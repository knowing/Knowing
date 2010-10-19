package de.lmu.ifi.dbs.medmon.sensor.core.sensor;

/**
 * Representing a generic sensor
 * 
 * @author Nepomuk Seiler
 * @version 0.5
 */
public interface ISensor<E> {

	public static final int MASTER = 1;
	public static final int SLAVE = 2;
	
	public String getVersion();
	
	public String getName();
	
	public int getType();
	
	public E[] getData();
	
}
