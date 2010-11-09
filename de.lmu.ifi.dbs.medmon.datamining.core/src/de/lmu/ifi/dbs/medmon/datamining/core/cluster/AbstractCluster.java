package de.lmu.ifi.dbs.medmon.datamining.core.cluster;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
public abstract class AbstractCluster {

	private final String label;

	public AbstractCluster(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	
}
