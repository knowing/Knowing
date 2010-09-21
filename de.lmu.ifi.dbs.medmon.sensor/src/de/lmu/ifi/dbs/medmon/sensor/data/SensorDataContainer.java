package de.lmu.ifi.dbs.medmon.sensor.data;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import de.lmu.ifi.dbs.medmon.database.model.SensorData;

public class SensorDataContainer {
	
	public static final int DAY 	= 0;
	public static final int WEEK 	= 1;
	public static final int MONTH 	= 2;
	
	private final HashMap<Date, SensorDataContainer> container;
	
	private final SensorData[] data;
	private Date date;
	private int type;
	
	public SensorDataContainer(SensorData[] data, int type) {
		container = new HashMap<Date, SensorDataContainer>(data.length);
		this.data = data;
		parse(type);
	}
		
	private void parse(int type) {	
		new QuickSort().sort(data);

		switch(type) {
		case DAY: parseDay();
		}
	}
	
	private void parseDay() {
		container.clear();
		GregorianCalendar cal = new GregorianCalendar();
		Vector<SensorData> tmp = new Vector<SensorData>();
		for(int i=0; i < data.length; i++) {
			tmp.add(data[i]);
			if(!isEqualDay(data[i].getRecorded(), data[i+1].getRecorded())) {
				//container.put(date, tmp.toArray(new SensorData[tmp.size()]));
			}		
		}
	}
	
	/**
	 * Compares to dates if they describe the same day
	 * @param date1
	 * @param date2
	 * @return true if both describe a equal day
	 */
	private boolean isEqualDay(Date date1, Date date2) {
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long day = 24 * 60 * 60 * 1000;
		//Differenz in days: 0 if equal and <1 if not
		long diff = Math.abs(time1 - time2) / day;
		return diff < day;		
	}

	private SensorData getData(Date date, int type) {
		return null;
	}
	
	
	private class QuickSort  {
		private SensorData[] numbers;
		private int number;

		public void sort(SensorData[] values) {
			// Check for empty or null array
			if (values ==null || values.length==0){
				return;
			}
			this.numbers = values;
			number = values.length;
			quicksort(0, number - 1);
		}

		private void quicksort(int low, int high) {
			int i = low, j = high;
			// Get the pivot element from the middle of the list
			SensorData pivot = numbers[low + (high-low)/2];

			// Divide into two lists
			while (i <= j) {
				// If the current value from the left list is smaller then the pivot
				// element then get the next element from the left list
				while (numbers[i].getRecorded().before(pivot.getRecorded())) {
					i++;
				}
				// If the current value from the right list is larger then the pivot
				// element then get the next element from the right list
				while (numbers[j].getRecorded().after(pivot.getRecorded())) {
					j--;
				}

				// If we have found a values in the left list which is larger then
				// the pivot element and if we have found a value in the right list
				// which is smaller then the pivot element then we exchange the
				// values.
				// As we are done we can increase i and j
				if (i <= j) {
					exchange(i, j);
					i++;
					j--;
				}
			}
			// Recursion
			if (low < j)
				quicksort(low, j);
			if (i < high)
				quicksort(i, high);
		}

		private void exchange(int i, int j) {
			SensorData temp = numbers[i];
			numbers[i] = numbers[j];
			numbers[j] = temp;
		}
	}

}
