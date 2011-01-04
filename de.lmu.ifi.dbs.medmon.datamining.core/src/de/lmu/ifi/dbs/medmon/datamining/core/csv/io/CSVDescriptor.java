package de.lmu.ifi.dbs.medmon.datamining.core.csv.io;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
		fieldSeparator = CSVFile.DEFAULT_FIELD_SEPARATOR;
		textQualifier = CSVFile.DEFAULT_TEXT_QUALIFIER;
	}

	/**
	 * 
	 * @param position
	 *            - Fieldposition
	 * @param type
	 *            - Fieldtype
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

	public void setFields(Map<Integer, Class> fields) {
		this.fields = fields;
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

	public int getNumericFieldCount() {
		int count = 0;
		for (Class clazz : fields.values()) {
			if (clazz == Double.class || clazz == Float.class || clazz == Long.class || clazz == Integer.class)
				count++;
		}
		return count;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("CSVDescriptor [fieldSeparator=");
		builder.append(fieldSeparator);
		builder.append(", textQualifier=");
		builder.append(textQualifier);
		builder.append(", datePattern=");
		builder.append(datePattern);
		builder.append(", fields=");
		builder.append(fields != null ? toString(fields.entrySet(), maxLen) : null);
		builder.append("]");
		return builder.toString();
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
}
