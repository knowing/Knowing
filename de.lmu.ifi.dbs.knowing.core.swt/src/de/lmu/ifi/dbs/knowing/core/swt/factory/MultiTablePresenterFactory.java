/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt.factory;

import java.util.Properties;

import org.eclipse.swt.widgets.Composite;

import de.lmu.ifi.dbs.knowing.core.factory.IPresenterFactory;
import de.lmu.ifi.dbs.knowing.core.processing.IPresenter;
import de.lmu.ifi.dbs.knowing.core.swt.MultiTablePresenter;

/**
 * @author Nepomuk Seiler
 * @version 0.4
 * @since 23.03.2011
 */
public class MultiTablePresenterFactory implements IPresenterFactory {

	public static final String ID = "de.lmu.ifi.dbs.knowing.core.swt.MultiTablePresenter";
	
	private static final Properties properties = new Properties();
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "Multi Table Presenter";
	}

	@Override
	public Properties getDefault() {
		return properties;
	}

	@Override
	public IPresenter<Composite> getInstance(Properties properties) {
		return new MultiTablePresenter();
	}

}
