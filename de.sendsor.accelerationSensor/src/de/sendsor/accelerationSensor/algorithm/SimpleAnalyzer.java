package de.sendsor.accelerationSensor.algorithm;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.AbstractAlgorithm;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.NumericParameter;
import de.lmu.ifi.dbs.medmon.sensor.core.processing.StringParameter;

public class SimpleAnalyzer<E extends Data> extends AbstractAlgorithm<E> {

	public static final String PIE_CHART = "Pie-Chart";
	public static final String BAR_CHART = "Bar-Chart";
	
	
	public SimpleAnalyzer() {
		init();
	}
	
	private void init() {
		NumericParameter toleranz = new NumericParameter("Toleranz", -10, 10, 0);
		StringParameter display = new StringParameter("Darstellung", new String[] {PIE_CHART, BAR_CHART});
		parameters.put(toleranz.getName(), toleranz);
		parameters.put(display.getName(), display);
	}

	@Override
	public IAnalyzedData process(Object data) {
		//TODO InputData missing
		return new SimpleAnalyzerData();
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
	
	@Override
	public Class<?> getDataClass() {
		return Data.class.getClass();
	}


}
