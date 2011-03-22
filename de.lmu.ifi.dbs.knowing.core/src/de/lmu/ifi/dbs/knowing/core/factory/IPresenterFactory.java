/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.factory;

import java.util.Properties;

import de.lmu.ifi.dbs.knowing.core.processing.IPresenter;


/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.03.2011
 */
public interface IPresenterFactory extends IFactory {

	public static final String PROP_UI_CLASS = "ui.class";
	
	@Override
	public IPresenter getInstance(Properties properties);
}
