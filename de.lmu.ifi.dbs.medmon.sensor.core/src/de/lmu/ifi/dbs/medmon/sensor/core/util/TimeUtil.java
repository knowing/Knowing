package de.lmu.ifi.dbs.medmon.sensor.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;

public class TimeUtil {

	public static int getCalendarConstant(Date firstTimestamp, Date lastTimestamp) {
		// Lazy initialize calendarConstant
		int calendarConstant = -1;
		if (firstTimestamp != null && lastTimestamp != null) {
			Calendar first = new GregorianCalendar();
			Calendar last = new GregorianCalendar();
			first.setTime(firstTimestamp);
			last.setTime(lastTimestamp);

			// Checking the Calendar Constant: General -> Detail
			if (first.get(Calendar.WEEK_OF_YEAR) == last.get(Calendar.WEEK_OF_YEAR)) // Same
																						// Week
				calendarConstant = Calendar.WEEK_OF_YEAR;
			if (first.get(Calendar.DAY_OF_YEAR) == last.get(Calendar.DAY_OF_YEAR)) // Same
																					// Day
				calendarConstant = Calendar.DAY_OF_YEAR;
			if (first.get(Calendar.HOUR_OF_DAY) == last.get(Calendar.HOUR_OF_DAY)) // Same
																					// Hour
				calendarConstant = Calendar.HOUR_OF_DAY;
		}
		return calendarConstant;
	}

	public static int getCalendarConstant(ContainerType type) {
		switch (type) {
		case HOUR:
			return Calendar.HOUR_OF_DAY;
		case DAY:
			return Calendar.DAY_OF_YEAR;
		case WEEK:
			return Calendar.WEEK_OF_YEAR;
		case MONTH:
			return Calendar.MONTH;
		case YEAR:
			return Calendar.YEAR;
		default:
			return -1;
		}
	}
	
	public static ContainerType getNext(ContainerType type) {
		switch(type) {
		case HOUR:
			return ContainerType.DAY;
		case DAY:
			return ContainerType.WEEK;
		case WEEK: 
			return ContainerType.MONTH;
		case MONTH:
			return ContainerType.YEAR;
		default: return ContainerType.YEAR;
		}
	}
}
