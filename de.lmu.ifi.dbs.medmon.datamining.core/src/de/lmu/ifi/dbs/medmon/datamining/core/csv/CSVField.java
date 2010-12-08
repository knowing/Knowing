package de.lmu.ifi.dbs.medmon.datamining.core.csv;

public class CSVField {

	private int position;
	
	private String formatter;
		
	private Class type;

	public CSVField(int position, String formatter, Class type) {
		this.position = position;
		this.formatter = formatter;
		this.type = type;
	}

	public CSVField(int position, Class type) {
		this(position, "", type);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}
	
	
	
}
