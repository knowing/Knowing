package de.lmu.ifi.dbs.knowing.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumericAttribute {

	/* Attribute name*/
	String name();
	
	/* Index */
	int index();
}
