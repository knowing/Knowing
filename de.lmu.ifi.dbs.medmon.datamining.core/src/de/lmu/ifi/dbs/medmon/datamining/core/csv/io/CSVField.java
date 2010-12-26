package de.lmu.ifi.dbs.medmon.datamining.core.csv.io;

public class CSVField {

	private int position;
			
	private Class type;

	public CSVField(int position, Class type) {
		this.position = position;
		this.type = type;
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

	@Override
	public String toString() {
		return "CSVField [position=" + position + ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + position;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CSVField other = (CSVField) obj;
		if (position != other.position)
			return false;
		return true;
	}
	
	
	
}
