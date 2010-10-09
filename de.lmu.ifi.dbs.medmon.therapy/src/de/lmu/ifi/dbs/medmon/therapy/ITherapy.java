package de.lmu.ifi.dbs.medmon.therapy;

import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;

/**
 * The Interface wraps therapy information for a certain disease
 * including an algorithm to analyse the data.
 * 
 * @author Nepomuk Seiler
 * @version 0.3.1
 */
public interface ITherapy {
		
	public IDisease getDisease();
	
	public ISensorDataAlgorithm getAnalysers();
	
	public String getName();
	
	public String getDescription();
}
