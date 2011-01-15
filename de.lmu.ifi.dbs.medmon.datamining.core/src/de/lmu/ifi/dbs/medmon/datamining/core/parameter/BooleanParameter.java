package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

public class BooleanParameter extends AbstractProcessorParameter<Boolean> {

	private Boolean value = Boolean.FALSE;

	public BooleanParameter(String name, boolean defaultValue) {
		super(name, BOOL_TYPE);
	}

	@Override
	public void setValue(Boolean value) {	
		fireParameterChanged(getName(), getValue(), value);
		this.value = value;
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public void setValueAsString(String value) {
		Boolean newValue = Boolean.valueOf(value);
		if(newValue != this.value)
			fireParameterChanged(getName(), this.value, newValue);
		this.value = newValue;
	}
	
	@Override
	public Boolean[] getValues() {
		return new Boolean[] { Boolean.TRUE, Boolean.FALSE };
	}

	@Override
	public boolean isValid(Boolean value) {
		return true;
	}
}
