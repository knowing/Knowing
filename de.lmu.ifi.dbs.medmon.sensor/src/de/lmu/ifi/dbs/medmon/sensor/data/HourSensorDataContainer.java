package de.lmu.ifi.dbs.medmon.sensor.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.Assert;

import de.lmu.ifi.dbs.medmon.database.model.Data;

public class HourSensorDataContainer extends AbstractSensorDataContainer {

	private final Calendar hour = new GregorianCalendar();

	public HourSensorDataContainer(ISensorDataContainer parent, Data[] data) {
		super(parent, ISensorDataContainer.HOUR, data);
		init(data);
	}

	public HourSensorDataContainer(Data[] data) {
		this(null, data);
	}

	private void init(Data[] data) {
		if (data == null)
			return;
		//Only valid entities
		Assert.isNotNull(data[0]);
		Assert.isNotNull(data[data.length - 1]);
		Calendar start = data[0].getId().getRecord();
		Calendar end = data[data.length - 1].getId().getRecord();
		// Assumption the array was parsed by
		// AbstractSensorDataContainer.parse(..)
		Assert.isTrue(start.get(Calendar.HOUR_OF_DAY) == end.get(Calendar.HOUR_OF_DAY));
		
		//
		hour.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
	}

	@Override
	public String getName() {
		SimpleDateFormat df = new SimpleDateFormat("hh");
		return df.format(hour.getTime());
	}

}
