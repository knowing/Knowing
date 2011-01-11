package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

public class StaticParameter implements IProcessorParameter<String> {

	private final String name;
	private final String value;

	public StaticParameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public StaticParameter(String name, int value) {
		this(name, String.valueOf(value));
	}
	
	public StaticParameter(String name, double value) {
		this(name, String.valueOf(value));
	}

	public StaticParameter(String name, boolean value) {
		this(name, String.valueOf(value));
	}

	@Override
	public String getType() {
		return STATIC_TYPE;
	}

	@Override
	public String[] getValues() {
		return new String[] { value };
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setValueAsString(String value) {
		// dot nothing
	}

	@Override
	public void setValue(String value) {
		// do nothing
	}

	@Override
	public boolean isValid(String value) {
		return true;
	}

}
