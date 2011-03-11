package de.lmu.ifi.dbs.medmon.datamining.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NominalAttribute {
	
	/* Attribute name*/
	String name();
	
	/* Index */
	int index();
	
	String[] attributeValues() default {};
}
