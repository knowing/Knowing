package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;

/**
 * Provides basic functionality for ISensorDataContainer to work as a tree node.
 * 
 * @author Nepomuk Seiler
 * 
 */
public abstract class AbstractSensorDataContainer<E> implements ISensorDataContainer {

	// null for root
	private ISensorDataContainer<E> parent;

	// never null; empty for leaf
	private final List<ISensorDataContainer<E>> children;

	//
	private E[] data;

	// Verifiy the tree level of this container
	private int type;

	// DataBlock
	protected Block block;

	public AbstractSensorDataContainer(ISensorDataContainer parent, int type, E[] data) {
		this.parent = parent;
		this.type = type;
		this.data = data;
		children = new ArrayList<ISensorDataContainer<E>>();

		if (parent != null)
			parent.addChild(this);
	}

	public AbstractSensorDataContainer(ISensorDataContainer<E> parent, int type, Block block) {
		this.parent = parent;
		this.type = type;
		this.block = block;
		children = new ArrayList<ISensorDataContainer<E>>();

		if (parent != null)
			parent.addChild(this);

	}

	public AbstractSensorDataContainer(int type, E[] data) {
		this(null, type, data);
	}

	@Override
	public ISensorDataContainer<E> getParent() {
		return parent;
	}

	@Override
	public ISensorDataContainer<E>[] getChildren() {
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

	@Override
	public Block getBlock() {
		return block;
	}

	/**
	 * Returns the SensorData[] if set or evaluate the data of its children
	 * 
	 * @throws IOException
	 */
	@Override
	public E[] getSensorData(IConverter converter) throws IOException {
		return (data == null) ? evaluateData(converter) : data;
	}

	/**
	 * Evaluating SensorData going recursive through the tree.
	 * 
	 * @return SensorData[] containing all elements
	 * @throws IOException
	 */
	protected E[] evaluateData(IConverter converter) throws IOException {
		if (block != null)
			return importData(block, converter);

		LinkedList<E> ret = new LinkedList<E>();
		for (ISensorDataContainer<E> container : children) {
			for (E data : container.getSensorData(converter)) {
				ret.add(data);
			}
		}
		E[] returns = (E[]) new Object[ret.size()];
		return ret.toArray(returns);
	}

	protected E[] importData(Block block, IConverter<E> converter) throws IOException {
		return converter.parseBlockToData(block);
	}

	// TODO Implement Listener Support

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
		AbstractSensorDataContainer<E> other = (AbstractSensorDataContainer<E>) obj;
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
