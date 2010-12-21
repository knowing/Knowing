package de.lmu.ifi.dbs.medmon.datamining.core.processing;


/**
 * 
 * @author Nepomuk Seiler
 * @version 1.2
 */
public abstract class AbstractAlgorithm extends AbstractDataProcessor implements IAlgorithm {
	
			
	public AbstractAlgorithm(String name, int dimension) {
		this(name, dimension, "", "");
	}
	
	public AbstractAlgorithm(String name, int dimension, String description, String version) {
		super(name, dimension, description, version);
		analyzedData.put(DEFAULT_DATA, null);
		analyzedData.put(TABLE_DATA, null);
	}

}
