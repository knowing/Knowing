package de.lmu.ifi.dbs.medmon.sensor.core.processing;


public interface IAlgorithm<E> extends IDataProcessor {

	@Override
	public IAnalyzedData process(Object data);
	
	/**
	 * For analyzing data in more than one step, this method
	 * takes the created IAnalyzedData object from his predecessor
	 * and use it for his IAnalyzedData Object.
	 * 
	 * @param data
	 * @param analyzedData
	 * @return IAnalyzedData
	 */
	public IAnalyzedData process(Object data, IAnalyzedData analyzedData);

}
