package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import de.lmu.ifi.dbs.medmon.datamining.core.analyzed.EmptyAnalyzedData;

/**
 * 
 * @author Nepomuk Seiler
 * @version 1.2
 */
public abstract class AbstractAlgorithm extends AbstractDataProcessor implements IAlgorithm {

	public AbstractAlgorithm(String name, int inputDimension, int outputDimension) {
		this(name, inputDimension, outputDimension, "", "");
	}

	public AbstractAlgorithm(String name, int inputDimension, int outputDimension, String description, String version) {
		super(name, inputDimension, outputDimension, description, version);
		analyzedData.put(DEFAULT_DATA, new EmptyAnalyzedData());
	}

}
