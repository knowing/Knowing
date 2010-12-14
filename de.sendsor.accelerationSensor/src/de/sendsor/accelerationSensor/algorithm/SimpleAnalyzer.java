package de.sendsor.accelerationSensor.algorithm;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jfree.data.time.Hour;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.NumericParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.StringParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.AbstractAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;

public class SimpleAnalyzer extends AbstractAlgorithm {

	public static final String PIE_CHART = "Pie-Chart";
	public static final String BAR_CHART = "Bar-Chart";
	
	public static final String NAME = "Simple Analyzer";
	
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
	public IAnalyzedData process(RawData data) {
		//SampleData Generation
		SimpleAnalyzerData analyzedData = SimpleAnalyzerData.getInstance();
		Hour[] hours = getHours();
		
		for(int i=0; i < hours.length; i += 3)
			analyzedData.addPeriod(hours[i], hours[i+1], Category.WALK);
		
		for(int i=0; i < hours.length; i += 7)
			analyzedData.addPeriod(hours[i], hours[i+2], Category.LIE);
		
		
		for(int i=0; i < hours.length; i += 2)
			analyzedData.addPeriod(hours[i], hours[i], Category.SIT);
		
		return analyzedData;
	}
	
	@Override
	public IAnalyzedData process(RawData data, IAnalyzedData analyzedData) {
		return null;
	}
	
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "A Simple Analyzer";
	}

	@Override
	public String getVersion() {
		return "0.2";
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

	@Override
	public int dimension() {
		return INDEFINITE_DIMENSION;
	}

}
