package knowing.test.factory;

import java.util.Properties;

import knowing.test.processor.SimpleKMeansProcessor;

import de.lmu.ifi.dbs.knowing.core.factory.IProcessorFactory;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;

public class KMeansFactory implements IProcessorFactory {

	public static final String ID = "weka.clusterers.SimpleKMeans";
	
	private Properties properties = new Properties();
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "SimpleKMeans Factory";
	}

	@Override
	public Properties getDefault() {
		return properties;
	}

	@Override
	public IProcessor getInstance(Properties properties) {
		return new SimpleKMeansProcessor();
	}

}
