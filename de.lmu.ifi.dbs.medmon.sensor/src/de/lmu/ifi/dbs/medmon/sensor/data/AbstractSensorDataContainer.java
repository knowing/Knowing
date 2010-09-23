package de.lmu.ifi.dbs.medmon.sensor.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import de.lmu.ifi.dbs.medmon.database.model.SensorData;

/**
 * Provides basic functionality for ISensorDataContainer
 * to work as a tree node.
 * 
 * @author Nepomuk Seiler
 *
 */
public abstract class AbstractSensorDataContainer implements
		ISensorDataContainer {

	// null for root
	private ISensorDataContainer parent;

	// never null; empty for leaf
	private final ArrayList<ISensorDataContainer> children;

	//
	private SensorData[] data;

	//Verifiy the tree level of this container
	private int type;

	public AbstractSensorDataContainer(ISensorDataContainer parent, int type, SensorData[] data) {
		this.parent = parent;
		this.type = type;
		this.data = data;
		children = new ArrayList<ISensorDataContainer>();
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
	 * Returns the SensorData[] if set or evaluate the data
	 * of its children
	 */
	@Override
	public SensorData[] getSensorData() {
		return (data == null) ? evaluateData() : data;
	}

	/**
	 * Evaluating SensorData going recursive through
	 * the tree.
	 * @return SensorData[] containing all elements
	 */
	private SensorData[] evaluateData() {
		LinkedList<SensorData> ret = new LinkedList<SensorData>();
		for(ISensorDataContainer container : children) {
			for(SensorData data : container.getSensorData()) {
				ret.add(data);
			}
		}
		return ret.toArray(new SensorData[ret.size()]);
	}
	
	//TODO Implement Listener Support

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
