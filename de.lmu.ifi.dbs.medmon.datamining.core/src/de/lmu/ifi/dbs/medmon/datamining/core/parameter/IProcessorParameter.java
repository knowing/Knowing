package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * Generic Interface for {@link ISensorDataAlgorithm} properties
 * 
 * This Interface is very similiar to {@link IPropertySource} and {@link IPropertyDescriptor}
 * Maybe in future releases this interface will be deprecated and uses the mentioned ones.
 * 
 * @author Nepomuk Seiler
 * @version 1.2
 */
public interface IProcessorParameter<E> {

	public static final String INT_TYPE = "int";
	public static final String STRING_TYPE = "string";
	public static final String BOOL_TYPE = "bool";
	public static final String CLUSTER_TYPE = "cluster";
	public static final String STATIC_TYPE = "static";
	
	/**
	 * To create a label describing the parameter
	 * @return Parametername 
	 */
	public String getName();
	
	
	/**
	 * 
	 * @return type name 
	 */
	public String getType();
	
	/**
	 * Generic method to display possible values
	 * @return
	 */
	public E[] getValues();
	
	/**
	 * Set the specific value. 
	 * @param value - should be 
	 * @return false for invalid value
	 */
	public void setValue(E value);
	
	public E getValue();
	
	public void setValueAsString(String value);
	
	/**
	 * Check if a value is valid
	 * @param value
	 * @return
	 */
	public boolean isValid(E value);
	
		
}
