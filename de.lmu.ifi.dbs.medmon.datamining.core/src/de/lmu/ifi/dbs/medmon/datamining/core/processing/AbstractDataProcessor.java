package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.util.HashMap;
import java.util.Map;

import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;

public abstract class AbstractDataProcessor implements IDataProcessor {

	protected final HashMap<String, IProcessorParameter> parameters = new HashMap<String, IProcessorParameter>();
	
	protected final HashMap<String, IAnalyzedData> analyzedData = new HashMap<String, IAnalyzedData>();
	
	private final String name;
	private final int in;
	private final int out;
	
	protected String description = "";
	protected String version = "0.0";
		
	public AbstractDataProcessor(String name, int inputDimension, int outputDimension) {
		this(name, inputDimension, outputDimension, "", "");
	}

	public AbstractDataProcessor(String name, int inputDimension, int outputDimension, String description, String version) {
		this.name = name;
		this.in = inputDimension;
		this.out = outputDimension;
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
	
	/**
	 * <p>The abstract implementation suggest that the parameter processor is
	 * after this processor instance.</p>
	 * 
	 * <p>To check if the processors output AND input is compatible a validator
	 * must perform two checks like this:</p>
	 * 
	 * <p>prevProcessor --check1-> processorToCheck --check2-> nextProcessor
	 * </p>
	 * @param processor - the processor to check with this one
	 * @return compatible?
	 */
	@Override
	public boolean isCompatible(IDataProcessor processor) {
		return processor.inputDimension() == outputDimension() || outputDimension() == INDEFINITE_DIMENSION;
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
	public int inputDimension() {
		return in;
	}
	
	@Override
	public int outputDimension() {
		return out;
	}

}
