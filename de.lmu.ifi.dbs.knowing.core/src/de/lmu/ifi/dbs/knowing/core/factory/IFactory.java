package de.lmu.ifi.dbs.knowing.core.factory;

import java.util.Properties;

/**
 * This interface can be used to register factories for special
 * services at the Service-Registry.
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public interface IFactory {

	/**
	 * @return unique Identifier for this particular factory
	 */
	String getId();
	
	/**
	 * @return
	 */
	String getName();
	
	/**
	 * This is the main factory method. 
	 * 
	 * @param properties - configure the instance being created
	 * @return the instance created by this factory
	 */
	Object getInstance(Properties properties);
	
	/**
	 * @return Default Properties
	 */
	Properties getDefault();
}
