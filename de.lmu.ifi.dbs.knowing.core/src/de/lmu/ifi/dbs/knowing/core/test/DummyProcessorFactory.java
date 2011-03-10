package de.lmu.ifi.dbs.knowing.core.test;

import java.util.Properties;

import de.lmu.ifi.dbs.knowing.core.factory.IProcessorFactory;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;

public class DummyProcessorFactory implements IProcessorFactory {

	private final String id;
	private final String name;
	
	public DummyProcessorFactory(String id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Properties getDefault() {
		return null;
	}

	@Override
	public IProcessor getInstance(Properties properties) {
		return new DummyProcessor();
	}

}
