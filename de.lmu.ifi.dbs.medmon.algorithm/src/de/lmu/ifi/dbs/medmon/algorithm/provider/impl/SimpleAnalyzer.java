package de.lmu.ifi.dbs.medmon.algorithm.provider.impl;

import java.util.Properties;

import de.lmu.ifi.dbs.medmon.algorithm.extension.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.database.model.SensorData;

public class SimpleAnalyzer implements ISensorDataAlgorithm {

	public static final String PIE_CHART = "Pie-Chart";
	public static final String BAR_CHART = "Bar-Chart";
	
	private final Properties properties = new Properties();
	
	public SimpleAnalyzer() {
		init();
	}
	
	private void init() {
		properties.put("Toleranz", 3);
		properties.put("Darstellung", PIE_CHART);
	}

	@Override
	public IAnalyzedData analyze(SensorData[] data) {
		//TODO InputData missing
		return new SimpleAnalyzerData();
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public String getName() {
		return "Simple Analyzer";
	}

	@Override
	public String getDescription() {
		return "A Simple Analyzer";
	}

	@Override
	public double getVersion() {
		return 1.0;
	}

}
