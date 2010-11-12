package de.lmu.ifi.dbs.medmon.datamining.core.processing;

public interface IFilter<E> extends IDataProcessor {
	
	@Override
	public E process(Object data);

}
