package de.lmu.ifi.dbs.medmon.algorithm.provider.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.lmu.ifi.dbs.medmon.algorithm.extension.IAlgorithmParameter;
import de.lmu.ifi.dbs.medmon.algorithm.extension.NumericParameter;
import de.lmu.ifi.dbs.medmon.algorithm.extension.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.algorithm.extension.StringParameter;
import de.lmu.ifi.dbs.medmon.database.model.Data;

public class SimpleAnalyzer implements ISensorDataAlgorithm {

	public static final String PIE_CHART = "Pie-Chart";
	public static final String BAR_CHART = "Bar-Chart";
	
	private final HashMap<String, IAlgorithmParameter> parameters;
	
	public SimpleAnalyzer() {
		parameters = new HashMap<String, IAlgorithmParameter>();
		init();
	}
	
	private void init() {
		NumericParameter toleranz = new NumericParameter("Toleranz", -10, 10, 0);
		StringParameter display = new StringParameter("Darstellung", new String[] {PIE_CHART, BAR_CHART});
		parameters.put(toleranz.getName(), toleranz);
		parameters.put(display.getName(), display);
	}

	@Override
	public IAnalyzedData analyze(Data[] data) {
		//TODO InputData missing
		return new SimpleAnalyzerData();
	}
	
	@Override
	public Map<String, IAlgorithmParameter> getParameters() {
		return parameters;
	}
	
	@Override
	public IAlgorithmParameter getParameter(String key) {
		return parameters.get(key);
	}

	@Override
	public IAlgorithmParameter setParameter(String key, IAlgorithmParameter parameter) {
		return parameters.put(key, parameter);
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
	public String getVersion() {
		return "0.2";
	}

}
