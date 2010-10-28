package de.lmu.ifi.dbs.medmon.sensor.core.processing;

/**
 * Represents different String values a user can choose.
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public class StringParameter implements IProcessorParameter<String> {

	private final String name;

	private final String[] values;
	
	private String value;

	public StringParameter(String name, String[] values) {
		this.name = name;
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
		if(isValid(value))
			this.value = value;
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
	public String getName() {
		return name;
	}

}
