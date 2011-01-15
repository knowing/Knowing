package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

import java.util.List;

public class ParameterGroup extends AbstractProcessorParameter<List<IProcessorParameter>> {

	public ParameterGroup(String name) {
		super(name, null);
	}

	@Override
	public List<IProcessorParameter>[] getValues() {
		return null;
	}

	@Override
	public void setValue(List<IProcessorParameter> value) {
				
	}

	@Override
	public List<IProcessorParameter> getValue() {
		return null;
	}

	@Override
	public void setValueAsString(String value) {
				
	}

	@Override
	public boolean isValid(List<IProcessorParameter> value) {
		
		return false;
	}

}
