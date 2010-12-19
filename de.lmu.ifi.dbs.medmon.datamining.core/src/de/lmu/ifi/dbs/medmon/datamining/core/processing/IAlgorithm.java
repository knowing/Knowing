package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.util.Map;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;

/**
 * <p>An Algorithm always returns an array of {@link IAnalyzedData} which 
 * is used to visualize the results. Via <code>analyzedDataDescriptions</code>
 * the selected IAnalyzedData is being visualized.
 * </p>
 * 
 * <p>Furthermore an algorithm should be capable of processing
 * {@link RawData} step by step, so the process method is extended.</p>
 * 
 * @author Nepomuk Seiler
 * @version 1.2
 */
public interface IAlgorithm extends IDataProcessor {

	public static final String DEFAULT_DATA = "Standard Output";
	public static final String TABLE_DATA   = "Table Output";
	public static final String CLUSTER_DATA = "Cluster Output";
	
	@Override
	public Map<String, IAnalyzedData> process(RawData data);
	
	/**
	 * For analyzing data in more than one step, this method
	 * takes the created Map<String, IAnalyzedData> object from his predecessor
	 * and use it for his Map<String, IAnalyzedData> Object.
	 * 
	 * @param data
	 * @param analyzedData
	 * @return Map<String, IAnalyzedData>
	 */
	public Map<String, IAnalyzedData> process(RawData data, Map<String, IAnalyzedData> analyzedData);
	
	/**
	 * An algorithm can work with or without timestamps.
	 * This method checks if the algorithm supports timesensitiv data.
	 * 
	 * @return processor needs timesensitiv data
	 */
	public boolean isTimeSensitiv();
	
	
	/**
	 * For UI to choose differente IAnalyzedData
	 * @return String - the keys of the Map<String, IAnalyzedData>
	 */
	public String[] analyzedDataKeys();
}
