/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt;

import java.util.Properties;

import org.eclipse.swt.widgets.Composite;

import de.lmu.ifi.dbs.knowing.core.factory.IPresenterFactory;
import de.lmu.ifi.dbs.knowing.core.processing.IPresenter;

/**
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 21.03.2011
 */
public class TablePresenterFactory implements IPresenterFactory {

	public static final String ID = "de.lmu.ifi.dbs.knowing.core.swt.TablePresenter";
	
	private static final Properties properties = new Properties();
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "Table Presenter";
	}

	@Override
	public Properties getDefault() {
		return properties;
	}

	@Override
	public IPresenter<Composite> getInstance(Properties properties) {
		return new TablePresenter();
	}

}
