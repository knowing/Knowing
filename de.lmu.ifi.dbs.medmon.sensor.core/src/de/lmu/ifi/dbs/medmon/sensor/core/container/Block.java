package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;

/**
 * Placeholder for a Block in a SensorFile
 * 
 * @author Nepomuk Seiler
 * @version 1.1
 */
public class Block {

	private final String file;
	private final int begin;
	private final int end;

	private int calendarConstant = -1;
	
	private Date firstTimestamp;
	private Date lastTimestamp;
	

	
	
	public Block(String file, int begin, int end, int calendarConstant, Date firstTimestamp, Date lastTimestamp) {
		this.file = file;
		this.begin = begin;
		this.end = end;
		this.calendarConstant = calendarConstant;
		this.firstTimestamp = firstTimestamp;
		this.lastTimestamp = lastTimestamp;
	}

	public Block(String file, int begin, int end, Date firstTimestamp, Date lastTimestamp) {
		this.file = file;
		this.begin = begin;
		this.end = end;
		this.firstTimestamp = firstTimestamp;
		this.lastTimestamp = lastTimestamp;		
	}
	
	public Block(String file, int begin, int end, int calendarConstant) {
		this(file, begin, end, null,null);
	}

	public String getFile() {
		return file;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}
	
	public Object[] importData(IConverter converter) throws IOException {
		return converter.parseBlockToData(this);
	}
	
	public Date getFirstTimestamp() throws IOException {			
		return firstTimestamp;
	}
	
	public Date getLastTimestamp() throws IOException {
		return lastTimestamp;
	}
	
	public int size() {
		//return (end - begin) * SDRConverter.CONTENT_BLOCK;
		return (end - begin) * 504;
	}
	
	public int getCalendarConstant() {
		//Lazy initialize calendarConstant
		if(calendarConstant == -1 && firstTimestamp != null && lastTimestamp != null) {
			Calendar first = new GregorianCalendar();
			Calendar last  = new GregorianCalendar();
			first.setTime(firstTimestamp);
			last.setTime(lastTimestamp);
			
			//Checking the Calendar Constant: General -> Detail
			if(first.get(Calendar.WEEK_OF_YEAR) == last.get(Calendar.WEEK_OF_YEAR)) //Same Week
				calendarConstant = Calendar.WEEK_OF_YEAR;
			if(first.get(Calendar.DAY_OF_YEAR) == last.get(Calendar.DAY_OF_YEAR))	//Same Day
				calendarConstant = Calendar.DAY_OF_YEAR;
			if(first.get(Calendar.HOUR_OF_DAY) == last.get(Calendar.HOUR_OF_DAY))	//Same Hour
				calendarConstant = Calendar.HOUR_OF_DAY;
		}
		return calendarConstant;
	}

	@Override
	public String toString() {
		return "Block [file=" + file + ", begin=" + begin + ", end=" + end + ", size=" + size() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + begin;
		result = prime * result + end;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}

	
	
}
