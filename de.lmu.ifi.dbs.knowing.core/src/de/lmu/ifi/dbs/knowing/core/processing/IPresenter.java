/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.processing;

import java.io.IOException;

import weka.core.Instances;

/**
 * @author muki
 * @version 0.1
 * @since 21.03.2011
 */
public interface IPresenter<T> {

	/**
	 * Creates the initial UI-Container presenting the data
	 * 
	 * @param <T>
	 * @param parent
	 * @return
	 */
	Object createContainer(T parent);
	
	/**
	 * <p>Adds a dataset set to the presentation.<br>
	 * Implementations could have different behaviour, e.g.
	 * <li> Extending existing presentation</li>
	 * <li> Creating new tab </li>
	 * <li> Dispose old and build new presentation </li>
	 * </p>
	 * 
	 * @param loader
	 * @throws IOException 
	 */
	void buildPresentation(ILoader loader) throws IOException;
	
	/**
	 * 
	 * @param processor
	 */
	void buildPresentation(IResultProcessor processor);
	
	/**
	 * <p>This method is used for determining if the<br>
	 * UI system is able to handle this presenter.</p>
	 * 
	 * @return the UI-Container class
	 */
	String getContainerClass();
	
	String getName();
}
