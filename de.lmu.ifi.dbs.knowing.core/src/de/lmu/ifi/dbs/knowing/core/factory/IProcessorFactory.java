package de.lmu.ifi.dbs.knowing.core.factory;

import java.util.Properties;

import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;

/**
 * Creates an IProcessor.
 * 
 * @author Nepomuk Seiler
 *
 */
public interface IProcessorFactory extends IFactory {

	@Override
	IProcessor getInstance(Properties properties);
}
