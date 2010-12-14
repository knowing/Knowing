package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;

public interface IFilter extends IDataProcessor {
	
	@Override
	public RawData process(RawData data);

}
