package de.lmu.ifi.dbs.medmon.datamining.core.csv;

import java.util.HashMap;
import java.util.Map;

public class CSVDescriptor {

	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd hh:mm:ss.SSS";  
	
    /**
     * The current char used as field separator.
     */
    private char fieldSeparator;
    /**
     * The current char used as text qualifier.
     */
    private char textQualifier;
    /**
     * Pattern to format dates
     */
    private String datePattern;
    /**
     * Mapping from fieldposition -> fieldtype
     */
    private Map<Integer, Class> fields = new HashMap<Integer, Class>();
    
    public CSVDescriptor() {
		datePattern = DEFAULT_DATE_PATTERN;
	}
    
    /**
     * 
     * @param position - Fieldposition
     * @param type - Fieldtype
     * @return the old fieldtype
     */
    public Class addField(int position, Class type) {
    	return fields.put(position, type);
    }
    
    public Class getField(int position) {
    	return fields.get(position);
    }
    
    public Map<Integer, Class> getFields() {
		return fields;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public char getFieldSeparator() {
		return fieldSeparator;
	}

	public void setFieldSeparator(char fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	public char getTextQualifier() {
		return textQualifier;
	}

	public void setTextQualifier(char textQualifier) {
		this.textQualifier = textQualifier;
	}
    
    
    
}
