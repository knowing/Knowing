package de.lmu.ifi.dbs.knowing.core.factory;

import java.util.Properties;

import weka.core.converters.Loader;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;

/**
 * Factory creating WEKA {@link Loader}s. Loaders are very
 * generic so they can be used for quite a few purposes.
 *  
 * @author Nepomuk Seiler
 *
 */
public interface ILoaderFactory extends IFactory {

	@Override
	ILoader getInstance(Properties properties);
}
