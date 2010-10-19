package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.io.IOException;

import de.lmu.ifi.dbs.medmon.database.model.Data;

/**
 * Interface to create a simple tree out of SensorData.
 * ISensorDataContainer wraps the SensorData and places
 * them in a tree. ISensorData acts like a Node in a tree. 
 * 
 * The programm can verifiy the tree level via the ISensorDataContainer type
 * e.g. DAY, WEEK or MONTH. 
 * 
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public interface ISensorDataContainer {
	
	//Level constants. Should be compared via % 10
	public static final int ROOT 	= 0;
	public static final int HOUR	= 2;
	public static final int DAY	 	= 3;
	public static final int WEEK 	= 4;
	public static final int MONTH 	= 5;
	public static final int BLOCK 	= 11;
	
	
	//Standard tree methods
	public ISensorDataContainer getParent();
	
	public ISensorDataContainer[] getChildren();
	
	public boolean addChild(ISensorDataContainer child);
	
	public boolean removeChild(ISensorDataContainer child);
	
	public boolean hasChildren();
	
	/**
	 * @return ISensorDataContainer.DAY | WEEK | MONTH
	 */
	public int getType();
	
	/**
	 * Provides the SensorData ownd by this container. It's possible
	 * that this could be null and the children contain the Data. 
	 * 
	 * @return SensorData[] below this node
	 * @throws IOException 
	 */
	public Data[] getSensorData() throws IOException;
	
	/**
	 * Used to display the Node correctly.
	 * e.g the record date of the SensorData (DAY) 
	 * @return Node name
	 */
	public String getName();
	
}
