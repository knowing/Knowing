package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeSensorDataContainer<E> extends AbstractSensorDataContainer<E> {

	protected String formatPattern = "dd.MM.yyyy - HH:mm";

	public TimeSensorDataContainer(ISensorDataContainer<E> parent, ContainerType type, Block block) {
		super(parent, type, block);
		initFormatPattern();
	}

	public TimeSensorDataContainer(ContainerType type, Block block) {
		this(null, type, block);
	}

	public TimeSensorDataContainer(ContainerType type) {
		this(null, type, null);
	}

	public TimeSensorDataContainer(ISensorDataContainer<E> parent, ContainerType type) {
		this(parent, type, null);
	}

	private void initFormatPattern() {
		switch (getType()) {
		case HOUR:
			formatPattern = "HH:mm";
			break;
		case DAY:
			formatPattern = "dd.MM.yyyy";
			break;
		case WEEK:
			formatPattern = "dd.MM.yyyy";
			break;
		}
	}

	@Override
	public Date getTimestamp() {
		return block.getDescriptor().getStartDate();
	}

	@Override
	public String getName() {
		SimpleDateFormat df = new SimpleDateFormat(formatPattern);
		Date date = block.getDescriptor().getStartDate();
		if (date == null)
			return "unkown";
		return df.format(date);
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
