package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeSensorDataContainer<E> extends AbstractSensorDataContainer<E> {

	protected String formatPattern = "dd.MM.yyyy - HH:mm";
	
	public TimeSensorDataContainer(ISensorDataContainer<E> parent, int type, Block block) {
		super(parent, type, block);
		initFormatPattern();
	}
	
	public TimeSensorDataContainer( int type, Block block) {
		this(null, type, block);
	}
		
	private void initFormatPattern() {
		switch(getType()) {
		case ISensorDataContainer.HOUR: formatPattern = "HH:mm"; break;
		case ISensorDataContainer.DAY: formatPattern = "dd.MM.yyyy"; break;
		case ISensorDataContainer.WEEK: formatPattern = "dd.MM.yyyy"; break;
		}
	}

	@Override
	public String getName() {
		SimpleDateFormat df = new SimpleDateFormat(formatPattern);
		Date date;
		try {
			date = block.getFirstTimestamp();
			if(date == null)
				return "unkown";
			return df.format(date);
		} catch (IOException e) {
			e.printStackTrace();
			return "Fehlerhafte Datei";
		}	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((formatPattern == null) ? 0 : formatPattern.hashCode());
		result = prime * result + ((block == null) ? 0 : block.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeSensorDataContainer<E> other = (TimeSensorDataContainer<E>) obj;
		if (block == null) {
			if (other.block != null)
				return false;
		} else if (!block.equals(other.block))
			return false;
		return true;
	}
	
	
}
