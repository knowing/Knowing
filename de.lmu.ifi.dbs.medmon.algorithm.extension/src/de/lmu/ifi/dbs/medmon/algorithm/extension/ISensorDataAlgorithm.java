package de.lmu.ifi.dbs.medmon.algorithm.extension;

import java.util.Map;

import de.lmu.ifi.dbs.medmon.database.model.Data;

/**
 * Generic Interface to create algorithms analyzing
 * SenorData.
 * 
 * @author Nepomuk Seiler
 * @version 1.2
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
	public IAnalyzedData analyze(Data[] data);
	
	/**
	 * If the algorithm has parameters, it should be configured
	 * with a Properties Object and be provided via this method
	 * 
	 * @return configuration properties for the algorithm
	 */
	public Map<String, IAlgorithmParameter> getParameters();
	
	public IAlgorithmParameter getParameter(String key);
	
	/**
	 * 
	 * @param key
	 * @param parameter
	 * @return previous value or null if none
	 */
	public IAlgorithmParameter setParameter(String key, IAlgorithmParameter parameter);
	
	public String getName();
	
	public String getDescription();
	
	public String getVersion();
}
