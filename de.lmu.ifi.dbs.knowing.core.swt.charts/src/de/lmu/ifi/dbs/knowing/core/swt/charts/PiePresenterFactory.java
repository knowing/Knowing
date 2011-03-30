/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt.charts;

import java.util.Properties;

import org.eclipse.swt.widgets.Composite;

import de.lmu.ifi.dbs.knowing.core.factory.IPresenterFactory;
import de.lmu.ifi.dbs.knowing.core.processing.IPresenter;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 30.03.2011
 *
 */
public class PiePresenterFactory implements IPresenterFactory {

	public static final String ID = "de.lmu.ifi.dbs.knowing.core.swt.charts.PiePresenter";
	private static final Properties properties = new Properties();
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "Pie Presenter";
	}

	@Override
	public Properties getDefault() {
		return properties;
	}

	@Override
	public IPresenter<Composite> getInstance(Properties properties) {
		return new PiePresenter();
	}

}
