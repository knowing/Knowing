package de.lmu.ifi.dbs.medmon.sensor.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.Assert;

import de.lmu.ifi.dbs.medmon.database.model.Data;

/**
 * Wraps SensorData[] for one day. Provides a date field
 * to access the date and displaying in the tree.
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 */
public class DaySensorDataContainer extends AbstractSensorDataContainer {
	
	private Date date;
	private Calendar calendar;
	
	public DaySensorDataContainer(ISensorDataContainer parent, Data[] data) {
		super(parent, DAY, data);
		init(data);
	}

	
	public DaySensorDataContainer(Data[] data) {
		this(null, data);
	}
	
	/**
	 * Checks and init the date field
	 * @param data
	 */
	private void init(Data[] data) {
		Assert.isNotNull(data);
		Calendar start = data[0].getId().getRecord();
		Calendar end	= data[data.length -1].getId().getRecord();
		
		//Simple Check: first and last array entry should have the same date
		//Assumption that the array is sorted
		Assert.isTrue(start.get(GregorianCalendar.DAY_OF_YEAR) == end.get(GregorianCalendar.DAY_OF_YEAR));
		date = start.getTime();
		calendar = start;
	}
	
	public Date getDate() {
		return date;
	}
	
	
	public Calendar getCalendar() {
		return calendar;
	}

	@Override
	public String getName() {
		SimpleDateFormat df = new SimpleDateFormat("dd.mm.yyy");
		System.out.println("DateFormat: " + df + " formats " + calendar);
		return df.format(calendar.getTime());
	}

}
