package de.lmu.ifi.dbs.medmon.sensor.core.processing;

import java.util.Map;

import de.lmu.ifi.dbs.medmon.sensor.core.parameter.IProcessorParameter;


public interface IDataProcessor {
	
	public static final String PROCESSOR_ID = "de.lmu.ifi.dbs.medmon.sensor.processing.processor";

	public Object process(Object data);
	
	public boolean isCompatible(IDataProcessor processor);
	
	public Class<?> getDataClass();
	
	/**
	 * If the algorithm has parameters, it should be configured
	 * with a Properties Object and be provided via this method
	 * 
	 * @return configuration properties for the algorithm
	 */
	public Map<String, IProcessorParameter> getParameters();
	
	public IProcessorParameter getParameter(String key);
	
	/**
	 * 
	 * @param key
	 * @param parameter
	 * @return previous value or null if none
	 */
	public IProcessorParameter setParameter(String key, IProcessorParameter parameter);
	
	public String getName();
	
	public String getDescription();
	
	public String getVersion();
	
	public String getID();
	
}
