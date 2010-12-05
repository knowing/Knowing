package de.lmu.ifi.dbs.medmon.datamining.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SensorData {

	public static final String DOUBLE = "double";
	public static final String LONG = "long";
	
	String type() default DOUBLE;
}
