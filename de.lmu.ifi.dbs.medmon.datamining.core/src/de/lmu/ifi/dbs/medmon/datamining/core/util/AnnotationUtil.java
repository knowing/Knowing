package de.lmu.ifi.dbs.medmon.datamining.core.util;

public class AnnotationUtil {

	public static String getGetterMethod(String field) {
		String startLetter = field.substring(0, 1).toUpperCase();
		return "get" + startLetter + field.substring(1, field.length());
	}
	
}
