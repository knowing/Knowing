package knowing.test.factory;

import java.util.Properties;

import knowing.test.processor.ExampleProcessor;

import de.lmu.ifi.dbs.knowing.core.factory.IProcessorFactory;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;

public class ExampleProcessorFactory implements IProcessorFactory {

	public static final String ID = "ExampleProcessorFactory";
	private static final Properties properties = new Properties();
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return ID;
	}

	@Override
	public Properties getDefault() {
		return properties;
	}

	@Override
	public IProcessor getInstance(Properties properties) {
		return new ExampleProcessor();
	}

}
