package de.lmu.ifi.dbs.medmon.sensor.core.processing;


public interface IAlgorithm<E> extends IDataProcessor {

	@Override
	public IAnalyzedData process(Object data);

}
