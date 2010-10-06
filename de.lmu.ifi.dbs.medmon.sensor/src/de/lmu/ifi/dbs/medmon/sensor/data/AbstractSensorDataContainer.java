package de.lmu.ifi.dbs.medmon.sensor.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import com.ibm.icu.impl.Grego;

import de.lmu.ifi.dbs.medmon.database.model.Data;

/**
 * Provides basic functionality for ISensorDataContainer to work as a tree node.
 * 
 * @author Nepomuk Seiler
 * 
 */
public abstract class AbstractSensorDataContainer implements
		ISensorDataContainer {

	// null for root
	private ISensorDataContainer parent;

	// never null; empty for leaf
	private final List<ISensorDataContainer> children;

	//
	private Data[] data;

	// Verifiy the tree level of this container
	private int type;

	public AbstractSensorDataContainer(ISensorDataContainer parent, int type,
			Data[] data) {
		this.parent = parent;
		this.type = type;
		this.data = data;
		children = new ArrayList<ISensorDataContainer>();

		if (parent != null)
			parent.addChild(this);
	}

	public AbstractSensorDataContainer(ISensorDataContainer parent, int type) {
		this(parent, type, null);
	}

	@Override
	public ISensorDataContainer getParent() {
		return parent;
	}

	@Override
	public ISensorDataContainer[] getChildren() {
		return children.toArray(new ISensorDataContainer[children.size()]);
	}

	@Override
	public boolean addChild(ISensorDataContainer child) {
		// Just give a warning for a logical error.
		// TODO use Logger
		/*
		 * if ((child.getType() % 10) >= (getType() % 10))
		 * System.out.println("Warning: Child " + child +
		 * " is same or higher level in hierachie");
		 */
		if (children.contains(child))
			return false;
		children.add(child);
		return true;
	}

	@Override
	public boolean removeChild(ISensorDataContainer child) {
		return children.remove(child);
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public int getType() {
		return type;
	}

	/**
	 * Returns the SensorData[] if set or evaluate the data of its children
	 */
	@Override
	public Data[] getSensorData() {
		return (data == null) ? evaluateData() : data;
	}

	/**
	 * Evaluating SensorData going recursive through the tree.
	 * 
	 * @return SensorData[] containing all elements
	 */
	private Data[] evaluateData() {
		LinkedList<Data> ret = new LinkedList<Data>();
		for (ISensorDataContainer container : children) {
			for (Data data : container.getSensorData()) {
				ret.add(data);
			}
		}
		return ret.toArray(new Data[ret.size()]);
	}

	// TODO Implement Listener Support

	// Library Methods
	/**
	 * Parses {@link Data} into {@link ISensorDataContainer}
	 * 
	 * @param Data
	 *            [] - the data which is parsed
	 * @param int rootType - ISensorDataContainer constant. Isn't used yet
	 * @param int leafType - ISensorDataContainer constant. Isn't used yet
	 */
	public static ISensorDataContainer parse(Data[] data, int rootType,
			int leafType) {
		RootSensorDataContainer root = new RootSensorDataContainer();
		List<ISensorDataContainer> days = parseDay(root, data);
		for (ISensorDataContainer each : days) {
			List<ISensorDataContainer> hours = parseHour(each,each.getSensorData());
		}
		return root;
	}

	public static ISensorDataContainer createContainer(int type,
			ISensorDataContainer parent, Data[] data) {
		switch (type) {
		case ISensorDataContainer.HOUR:
			return new HourSensorDataContainer(parent, data);
		case ISensorDataContainer.DAY:
			return new DaySensorDataContainer(parent, data);
		case ISensorDataContainer.WEEK:
			return null;
		case ISensorDataContainer.MONTH:
			return null;
		default:
			return new RootSensorDataContainer();
		}
	}

	public static List<ISensorDataContainer> parseDay(ISensorDataContainer parent, Data[] data) {
		Assert.isNotNull(data);
		if (data[0] == null)
			return Collections.emptyList();
		
		System.out.println("------------Parse Day---------------");	
		System.out.println("DataArray: " + data);
		int start = 0;
		int offset = 0;	
		Calendar startTime = data[start].getId().getRecord();
		Calendar endTime	= data[offset].getId().getRecord();
		System.out.println("StartTime: " + startTime.getTime());
		
		//First and last Data is same day
		if(startTime.get(Calendar.DAY_OF_YEAR) == data[data.length - 1].getId().getRecord().get(Calendar.DAY_OF_YEAR)) {
			ISensorDataContainer singleton = new DaySensorDataContainer(parent, data);
			return Collections.singletonList(singleton);
		}
					
		LinkedList<ISensorDataContainer> returns = new LinkedList<ISensorDataContainer>();
		while (start < data.length) {
			while (offset < data.length	&&
					data[start].getId().getRecord().get(Calendar.DAY_OF_YEAR) == 
					data[offset].getId().getRecord().get(Calendar.DAY_OF_YEAR)) {
				endTime	= data[offset].getId().getRecord();
				offset++;
			}
			// Calculating array length
			int length = offset - start - 1;
			Data[] containerArray = new Data[length];
			System.arraycopy(data, start, containerArray, 0, length);
			// Add the newling formed array
			returns.add(new DaySensorDataContainer(parent, containerArray));

			System.out.println("Array from: [" + start + "] to [" + offset+ "]" + " length=" + length);
			start = offset;
		}

		return returns;
	}
	
	public static List<ISensorDataContainer> parseHour(ISensorDataContainer parent, Data[] data) {
		Assert.isNotNull(data);
		if (data[0] == null)
			return Collections.emptyList();
		
		System.out.println("----------Parse Hour-------------");
		System.out.println("DataArray: " + data);
		int start = 0;
		int offset = 0;	
		Calendar startTime = data[start].getId().getRecord();
		Calendar endTime	= data[offset].getId().getRecord();
		System.out.println("StartTime: " + startTime.getTime());
		
		LinkedList<ISensorDataContainer> returns = new LinkedList<ISensorDataContainer>();
		while (start < data.length) {
			while (offset < data.length	&&
					data[start].getId().getRecord().get(Calendar.HOUR_OF_DAY) == 
					data[offset].getId().getRecord().get(Calendar.HOUR_OF_DAY)) {
				endTime	= data[offset].getId().getRecord();
				offset++;
			}
			// Calculating array length
			int length = offset - start - 1;
			Data[] containerArray = new Data[length];
			System.arraycopy(data, start, containerArray, 0, length);
			// Add the newling formed array
			returns.add(new HourSensorDataContainer(parent, containerArray));

			System.out.println("Array from: [" + start + "] to [" + offset+ "]" + " length=" + length);
			start = offset;
		}

		return returns;
	}

	// Standard Methods

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + type;
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
		AbstractSensorDataContainer other = (AbstractSensorDataContainer) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
