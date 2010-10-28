package de.lmu.ifi.dbs.medmon.sensor.core.processing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class DataProcessingList implements Serializable {

	private static final long serialVersionUID = 4399885420142543467L;
	
	private final ArrayList<IDataProcessor> processors = new ArrayList<IDataProcessor>();
	
	private String name;
	
	
	public DataProcessingList(String name) {
		this.name = name;
	}

	public boolean isEmpty() {
		return processors.isEmpty();
	}

	public boolean contains(Object o) {
		return processors.contains(o);
	}

	public int indexOf(Object o) {
		return processors.indexOf(o);
	}

	public IDataProcessor get(int index) {
		return processors.get(index);
	}

	public boolean add(IDataProcessor e) {
		return processors.add(e);
	}

	public void add(int index, IDataProcessor element) {
		processors.add(index, element);
	}

	public void clear() {
		processors.clear();
	}

	public Iterator<IDataProcessor> iterator() {
		return processors.iterator();
	}

	public int size() {
		return processors.size();
	}

	public IDataProcessor remove(int index) {
		return processors.remove(index);
	}

	public boolean remove(Object o) {
		return processors.remove(o);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
