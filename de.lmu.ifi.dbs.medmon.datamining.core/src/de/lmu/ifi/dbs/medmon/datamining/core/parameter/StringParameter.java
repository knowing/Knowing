package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

/**
 * Represents different String values a user can choose.
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public class StringParameter extends AbstractProcessorParameter<String> {

	private final String[] values;
	
	private String value;

	public StringParameter(String name, String[] values) {
		super(name, STRING_TYPE);
		this.values = values;
	}

	@Override
	public String[] getValues() {
		return values;
	}
	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		if(isValid(value)) {
			fireParameterChanged(getName(), getValue(), value);
			this.value = value;
		}
		
	}
	
	@Override
	public boolean isValid(String value) {
		for (String validValue : values) {
			if (validValue.equals(value))
				return true;
		}
		return false;
	}
		
	@Override
	public void setValueAsString(String value) {
		setValue(value);		
	}

}
