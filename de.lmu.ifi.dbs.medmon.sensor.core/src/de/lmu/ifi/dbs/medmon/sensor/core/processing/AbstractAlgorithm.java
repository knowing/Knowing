package de.lmu.ifi.dbs.medmon.sensor.core.processing;

import java.util.HashMap;
import java.util.Map;

import de.lmu.ifi.dbs.medmon.sensor.core.parameter.IProcessorParameter;

public abstract class AbstractAlgorithm<E> implements IAlgorithm<E> {
	
	protected final HashMap<String, IProcessorParameter> parameters = new HashMap<String, IProcessorParameter>();
	
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
		return processor.getClass() == getDataClass();
	}
	
}
