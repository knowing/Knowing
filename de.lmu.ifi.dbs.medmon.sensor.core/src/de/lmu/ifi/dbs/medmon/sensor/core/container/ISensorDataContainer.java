package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.io.IOException;
import java.util.Date;

import org.eclipse.core.runtime.IAdaptable;

import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;

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
 * @version 1.3
 */
public interface ISensorDataContainer<E>{
	
	//Level constants. Should be compared via % 10
	//TODO use Enumeration ContainerType
	/*
	 * 	public static final int ROOT 	= ContainerType.ROOT.ordinal();
	public static final int HOUR	= ContainerType.HOUR.ordinal();
	public static final int DAY	 	= ContainerType.DAY.ordinal();
	public static final int WEEK 	= ContainerType.WEEK.ordinal();
	public static final int MONTH 	= ContainerType.MONTH.ordinal();
	 */
	public static final int ROOT 	= 0;
	public static final int HOUR	= 2;
	public static final int DAY	 	= 3;
	public static final int WEEK 	= 4;
	public static final int MONTH 	= 5;
	public static final int BLOCK 	= 11;
	
	
	//Standard tree methods
	public ISensorDataContainer<E> getParent();
	
	public void setParent(ISensorDataContainer<E> parent);
	
	public ISensorDataContainer<E>[] getChildren();
	
	public boolean addChild(ISensorDataContainer<E> child);
	
	public boolean removeChild(ISensorDataContainer<E> child);
	
	public boolean hasChildren();
	
	/**
	 * @return ISensorDataContainer.DAY | WEEK | MONTH
	 */
	public ContainerType getType();
	
	/**
	 * Provides the SensorData ownd by this container. It's possible
	 * that this could be null and the children contain the Data. 
	 * 
	 * @return SensorData[] below this node
	 * @throws IOException 
	 */
	public E[] getSensorData(IConverter<E> converter) throws IOException;
	
	/**
	 * 
	 * @return the Block placeholder
	 */
	public Block getBlock();
	
	public void setBlock(Block block);
	
	/**
	 * Used to display the Node correctly.
	 * e.g the record date of the SensorData (DAY) 
	 * @return Node name
	 */
	public String getName();
	
	@Deprecated
	public Date getTimestamp();
	
}
