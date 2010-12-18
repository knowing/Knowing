package de.sendsor.accelerationSensor.algorithm;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.jfree.data.time.Hour;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.NumericParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.StringParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.AbstractAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;

public class SimpleAnalyzer extends AbstractAlgorithm {

	public static final String NAME = "Simple Analyzer";
	
	private static final String PIE_CHART = "Pie-Chart";
	private static final String BAR_CHART = "Bar-Chart";
		
	public SimpleAnalyzer() {
		super(NAME, INDEFINITE_DIMENSION);
		init();
	}
	
	private void init() {
		NumericParameter toleranz = new NumericParameter("Toleranz", -10, 10, 0);
		StringParameter display = new StringParameter("Darstellung", new String[] {PIE_CHART, BAR_CHART}); //Obsolete
		parameters.put(toleranz.getName(), toleranz);
		parameters.put(display.getName(), display);
		
		analyzedData.put(PIE_CHART, null);
		analyzedData.put(BAR_CHART, null);
		
		description = "A Simple Sample Analyzer";
		version = "0.4";
	}

	@Override
	public Map<String, IAnalyzedData> process(RawData data) {
		//SampleData Generation
		SimpleAnalyzerData barData = SimpleAnalyzerData.getInstance();
		Hour[] hours = getHours();
		
		for(int i=0; i < hours.length; i += 3)
			barData.addPeriod(hours[i], hours[i+1], Category.WALK);
		
		for(int i=0; i < hours.length; i += 7)
			barData.addPeriod(hours[i], hours[i+2], Category.LIE);
		
		
		for(int i=0; i < hours.length; i += 2)
			barData.addPeriod(hours[i], hours[i], Category.SIT);
	
		analyzedData.put(BAR_CHART, barData);
		analyzedData.put(DEFAULT_DATA, barData);
		return analyzedData;
	}
	
	@Override
	public Map<String, IAnalyzedData> process(RawData data, Map<String, IAnalyzedData> analyzedData) {
		return null;
	}
	
	@Override
	public String[] analyzedDataKeys() {
		return new String[] { PIE_CHART, BAR_CHART };
	}
	
	
	private static Hour[] getHours() {
		Hour[] returns = new Hour[48];
		Calendar cal = new GregorianCalendar();
		
		for(int i=0; i < returns.length; i++) {
			cal.add(Calendar.HOUR_OF_DAY, 1);
			returns[i] = new Hour(cal.getTime());
		}
			
		return returns;
	}

	@Override
	public boolean isTimeSensitiv() {
		return false;
	}

}
