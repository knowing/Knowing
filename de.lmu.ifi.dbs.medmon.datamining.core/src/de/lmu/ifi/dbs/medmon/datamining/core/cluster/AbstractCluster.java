package de.lmu.ifi.dbs.medmon.datamining.core.cluster;


/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
public abstract class AbstractCluster<E> implements ICluster<E>{

	protected String label;

	public AbstractCluster() {
		
	}
	
	public AbstractCluster(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
		
	
}
