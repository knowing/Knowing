package de.lmu.ifi.dbs.medmon.sensor.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.Assert;

import de.lmu.ifi.dbs.medmon.database.model.SensorData;

/**
 * Wraps SensorData[] for one day. Provides a date field
 * to access the date and displaying in the tree.
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public class DaySensorDataContainer extends AbstractSensorDataContainer {
	
	private Date date;
	
	public DaySensorDataContainer(ISensorDataContainer parent, SensorData[] data) {
		super(parent, DAY, data);
		init(data);
	}

	
	public DaySensorDataContainer(SensorData[] data) {
		this(null, data);
	}
	
	/**
	 * Checks and init the date field
	 * @param data
	 */
	private void init(SensorData[] data) {
		Assert.isNotNull(data);
		GregorianCalendar start = new GregorianCalendar();
		GregorianCalendar end	= new GregorianCalendar();
		
		start.setTime(data[0].getRecorded());
		end.setTime(data[data.length -1].getRecorded());
		//Simple Check: first and last array entry should have the same date
		//Assumption that the array is sorted
		Assert.isTrue(start.get(GregorianCalendar.DAY_OF_YEAR) == end.get(GregorianCalendar.DAY_OF_YEAR));
		date = data[0].getRecorded();
	}
	
	public Date getDate() {
		return date;
	}

	@Override
	public String getName() {
		SimpleDateFormat df = new SimpleDateFormat("dd.mm.yyy");
		return df.format(date);
	}

}
