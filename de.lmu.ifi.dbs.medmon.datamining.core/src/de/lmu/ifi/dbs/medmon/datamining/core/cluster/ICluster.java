package de.lmu.ifi.dbs.medmon.datamining.core.cluster;

import java.util.List;

public interface ICluster<E> {

	public String getLabel();
	
	public List<E> getValues();
		
}
