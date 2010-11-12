package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

/**
 * Generic Interface for {@link ISensorDataAlgorithm} properties
 * 
 * @author Nepomuk Seiler
 * @version 1.2
 */
public interface IProcessorParameter<E> {

	/**
	 * To create a label describing the parameter
	 * @return Parametername 
	 */
	public String getName();
	
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
	
	/**
	 * Check if a value is valid
	 * @param value
	 * @return
	 */
	public boolean isValid(E value);
		
}
