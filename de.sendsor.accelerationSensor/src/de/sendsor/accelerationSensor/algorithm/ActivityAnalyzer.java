package de.sendsor.accelerationSensor.algorithm;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

import de.lmu.ifi.dbs.medmon.base.ui.analyzed.ScatterAnalyzedData;
import de.lmu.ifi.dbs.medmon.base.ui.analyzed.TimePeriodValuesAnalyzedData;
import de.lmu.ifi.dbs.medmon.base.ui.analyzed.XYAreaAnalyedData;
import de.lmu.ifi.dbs.medmon.datamining.core.analyzed.TableAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.NumericParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.AbstractAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;

/**
 * Naive implementation of an activity level monitor
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
public class ActivityAnalyzer extends AbstractAlgorithm {

	public static final String NAME = "Activity Visualizer";
	private static final String ACTIVITY_LEVEL_CHART = "Activity Level";
	private static final String ACTIVITY_LEVEL_SCATTER = "Activity Scatter";
	private static final String ACTIVITY_LEVEL_TIME = "Activity-Over-Time";

	private final String X_PARAMETER = "Importance x";
	private final String Y_PARAMETER = "Importance y";
	private final String Z_PARAMETER = "Importance z";

	private double xBalance = 1.0;
	private double yBalance = 1.0;
	private double zBalance = 1.0;

	private final int PERIOD_SIZE = 25;

	public ActivityAnalyzer() {
		super(NAME, 3, INDEFINITE_DIMENSION);
		init();
	}

	private void init() {
		//Area dataset
		XYAreaAnalyedData dataset = new XYAreaAnalyedData();
		dataset.addSeries(new TimeSeries("Level"));
		analyzedData.put(ACTIVITY_LEVEL_CHART, dataset);
		
		//Scatter dataset
		ScatterAnalyzedData scatterSet = new ScatterAnalyzedData(ScatterAnalyzedData.PLAIN_DOTS);
		analyzedData.put(ACTIVITY_LEVEL_SCATTER, scatterSet);
		
		//Time dataset
		TimePeriodValuesAnalyzedData timeSet = new TimePeriodValuesAnalyzedData();
		analyzedData.put(ACTIVITY_LEVEL_TIME, timeSet);
		
		//Table dataset
		String[] cols= new String[] {"Date", "x", "y", "z", "val", "factors"};
		analyzedData.put(TABLE_DATA, TableAnalyzedData.getInstance(cols));

		parameters.put(X_PARAMETER, new NumericParameter(X_PARAMETER, 0, 100, 100));
		parameters.put(Y_PARAMETER, new NumericParameter(Y_PARAMETER, 0, 100, 100));
		parameters.put(Z_PARAMETER, new NumericParameter(Z_PARAMETER, 0, 100, 100));

		description = "Analysiert Aktivitaetslevel";
		version = "0.1 ALPHA";
	}

	@Override
	public Map<String, IAnalyzedData> process(RawData data) {
		Assert.isTrue(data.dimension() == 3);
		initBalances();
		XYAreaAnalyedData areaSet = (XYAreaAnalyedData) analyzedData.get(ACTIVITY_LEVEL_CHART);
		ScatterAnalyzedData scatterSet = (ScatterAnalyzedData) analyzedData.get(ACTIVITY_LEVEL_SCATTER);
		TimePeriodValuesAnalyzedData timeSet = (TimePeriodValuesAnalyzedData) analyzedData.get(ACTIVITY_LEVEL_TIME);
		TableAnalyzedData table = (TableAnalyzedData) analyzedData.get(TABLE_DATA);
		int size = data.getDimension(0).length;
		int period = 0;

		int x = 0;
		int y = 0;
		int z = 0;

		for (int i = 0; i < size; i++) {
			if (period == PERIOD_SIZE) {
				long date = data.getTimestamp()[i];
				Second second = new Second(new Date(date));
				x /=  PERIOD_SIZE;
				y /=  PERIOD_SIZE;
				z /=  PERIOD_SIZE;
				areaSet.addPeriod("Level", getWeightedValue(x, y, z), second);
				scatterSet.add("xy", x, y);
				scatterSet.add("xz", x, z);
				scatterSet.add("yz", y, z);
				timeSet.add("x", new Second(new Date(date)), x);
				timeSet.add("y", new Second(new Date(date)), y);
				timeSet.add("z", new Second(new Date(date)), z);
				table.addRow(createRow(x, y, z, date));
				x = 0;
				y = 0;
				z = 0;
				period = 0;
			}
			x += data.getDimension(0)[i];
			y += data.getDimension(1)[i];
			z += data.getDimension(2)[i];
			period++;
		}
		// Forget last values
		return analyzedData;
	}
	
	private String[] createRow(double x, double y, double z, long date) {
		String[] row = new String[6];
		DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
		row[0] = df.format(new Date(date));
		row[1] = String.valueOf(x);
		row[2] = String.valueOf(y);
		row[3] = String.valueOf(z);
		row[4] = String.valueOf(getWeightedValue(x, y, z));
		row[5] = String.valueOf(xBalance) + "|" + String.valueOf(yBalance) + "|" + String.valueOf(zBalance);
		return row;
	}

	private double getWeightedValue(double x, double y, double z) {
		return (x*xBalance) + (y*yBalance) + (z*zBalance) ;
	}

	private void initBalances() {
		int xWeight = ((NumericParameter) parameters.get(X_PARAMETER)).getValue();
		int yWeight = ((NumericParameter) parameters.get(Y_PARAMETER)).getValue();
		int zWeight = ((NumericParameter) parameters.get(Z_PARAMETER)).getValue();

		// everything 100%
		if (xWeight + yWeight + zWeight == 300)
			return;

		//z reduced
		if (zWeight < 100 && yWeight == 100 && xWeight == 100) { 
			//z reduced
			zBalance = ((double) zWeight) / 100.0;
			double diff = (100.0 - zWeight) / 2.0;
			yBalance = (yWeight + diff) / 100.0;
			xBalance = (xWeight + diff) / 100.0;
		} else if (yWeight < 100 && zWeight == 100 && xWeight == 100) {
			//y reduced
			yBalance = ((double) yWeight) / 100.0;
			double diff = (100.0 - yWeight) / 2.0;
			zBalance = (zWeight + diff) / 100.0;
			xBalance = (xWeight + diff) / 100.0;
		} else if (xWeight < 100 && zWeight == 100 && yWeight == 100) {
			//x reduced
			xBalance = ((double) xWeight) / 100.0;
			double diff = (100.0 - xWeight) / 2.0;
			zBalance = (zWeight + diff) / 100.0;
			yBalance = (yWeight + diff) / 100.0;
		}  else if (xWeight < 100 && yWeight < 100 && zWeight == 100) {
			//x,y reduced
			xBalance = ((double) xWeight) / 100.0;
			yBalance = ((double) yWeight) / 100.0;
			double diff = (100.0 - xWeight) + ((100.0 - yWeight));
			zBalance = (zWeight + diff) / 100.0;		
		}  else if (xWeight < 100 && yWeight < 100 && zWeight == 100) {
			//x,z reduced
			xBalance = ((double) xWeight) / 100.0;
			zBalance = ((double) zWeight) / 100.0;
			double diff = (100.0 - xWeight) + ((100.0 - zWeight));
			yBalance = (yWeight + diff) / 100.0;		
		}  else if (xWeight < 100 && yWeight < 100 && zWeight == 100) {
			//y,z reduced
			yBalance = ((double) yWeight) / 100.0;
			zBalance = ((double) zWeight) / 100.0;
			double diff = (100.0 - zWeight) + ((100.0 - yWeight));
			yBalance = (yWeight + diff) / 100.0;		
		}
	}

	@Override
	public Map<String, IAnalyzedData> process(RawData data, Map<String, IAnalyzedData> analyzedData) {
		return analyzedData;
	}

	@Override
	public boolean isTimeSensitiv() {
		return true;
	}

	@Override
	public String[] analyzedDataKeys() {
		return new String[] { ACTIVITY_LEVEL_CHART, ACTIVITY_LEVEL_SCATTER, ACTIVITY_LEVEL_TIME, TABLE_DATA };
	}

}
