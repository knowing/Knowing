package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;

/**
 * <p>An Algorithm always returns {@link IAnalyzedData} which is used
 * to visualize the results.</p>
 * 
 * <p>Furthermore an algorithm should be capable of processing
 * {@link RawData} step by step, so the process method is extended.</p>
 * 
 * @author Nepomuk Seiler
 */
public interface IAlgorithm extends IDataProcessor {

	@Override
	public IAnalyzedData[] process(RawData data);
	
	/**
	 * For analyzing data in more than one step, this method
	 * takes the created IAnalyzedData object from his predecessor
	 * and use it for his IAnalyzedData Object.
	 * 
	 * @param data
	 * @param analyzedData
	 * @return IAnalyzedData
	 */
	public IAnalyzedData[] process(RawData data, IAnalyzedData[] analyzedData);
	
	/**
	 * An algorithm can work with or without timestamps.
	 * This method checks if the algorithm supports timesensitiv data.
	 * 
	 * @return processor needs timesensitiv data
	 */
	public boolean isTimeSensitiv();

}
