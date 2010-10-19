package de.lmu.ifi.dbs.medmon.therapy.core.extensions;

/**
 * A disease is only describe by its possible therapies.
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public interface IDisease {
	
	public static final String DISEASE_ID = "de.lmu.ifi.dbs.medmon.disease";

	public String getName();
	
	public String getDescription();
	
	public ITherapy[] getTherapies();
}
