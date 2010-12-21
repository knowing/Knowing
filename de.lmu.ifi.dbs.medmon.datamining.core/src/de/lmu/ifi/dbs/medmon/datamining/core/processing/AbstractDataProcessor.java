package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.util.HashMap;
import java.util.Map;

import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;

public abstract class AbstractDataProcessor implements IDataProcessor {

	protected final HashMap<String, IProcessorParameter> parameters = new HashMap<String, IProcessorParameter>();
	
	protected final HashMap<String, IAnalyzedData> analyzedData = new HashMap<String, IAnalyzedData>();
	
	private final String name;
	private final int dimension;
	
	protected String description = "";
	protected String version = "0.0";
		
	public AbstractDataProcessor(String name, int dimension) {
		this(name, dimension, "", "");
	}

	public AbstractDataProcessor(String name, int dimension, String description, String version) {
		this.name = name;
		this.dimension = dimension;
		this.description = description;
		this.version = version;
	}

	@Override
	public Map<String, IProcessorParameter> getParameters() {
		return parameters;
	}
	
	@Override
	public IProcessorParameter getParameter(String key) {
		return parameters.get(key);
	}

	@Override
	public IProcessorParameter setParameter(String key, IProcessorParameter parameter) {
		return parameters.put(key, parameter);
	}
	
	@Override
	public boolean isCompatible(IDataProcessor processor) {
		return processor.dimension() == dimension() || dimension() == INDEFINITE_DIMENSION;
	}
	
	@Override
	public String getId() {
		return getClass().getName() + "." + getName() + "." + getVersion();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String getVersion() {
		return version;
	}
	
	@Override
	public int dimension() {
		return dimension;
	}

}
