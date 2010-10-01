package de.lmu.ifi.dbs.medmon.therapy;

/**
 * A disease is only describe by its possible therapies.
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public interface IDisease {

	public String getName();
	
	public String getDescription();
	
	public ITherapy[] getTherapies();
}
