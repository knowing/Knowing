package de.lmu.ifi.dbs.medmon.algorithm.extension;

import java.util.Properties;

import de.lmu.ifi.dbs.medmon.database.model.SensorData;

/**
 * Generic Interface to create algorithms analyzing
 * SenorData.
 * 
 * @author muki
 * @version 0.1
 */
public interface ISensorDataAlgorithm {

	public static final String ALGORITHM_ID = "de.lmu.ifi.dbs.medmon.algorithm";
	
	/**
	 * Provides the functionality of the Algorithm
	 * 
	 * TODO return Object must be specified
	 * @param data - SensorData which should be analyzed
	 * @return the new analyzed Object. 
	 */
	public Object analyze(SensorData[] data);
	
	/**
	 * If the algorithm has parameters, it should be configured
	 * with a Properties Object and be provided via this method
	 * 
	 * @return configuration properties for the algorithm
	 */
	public Properties getProperties();
	
	public String getName();
	
	public String getDescription();
	
	public double getVersion();
}
