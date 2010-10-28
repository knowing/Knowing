package de.lmu.ifi.dbs.medmon.sensor.core.processing;

public interface IFilter<E> extends IDataProcessor {
	
	@Override
	public E process(Object data);

}
