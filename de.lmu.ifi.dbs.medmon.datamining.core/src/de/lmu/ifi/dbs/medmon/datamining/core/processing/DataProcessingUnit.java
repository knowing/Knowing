package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

//This statement means that class "DataProcessingUnit.java" is the root-element 
//(namespace = "de.lmu.ifi.dbs.medmon.datamining.core.processing")
@XmlRootElement
public class DataProcessingUnit{
	
	private List<DataProcessor> processors;
	
	private String name = "default";
	
	public DataProcessingUnit() {
		processors = new LinkedList<DataProcessor>();
	}
	
	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * For interacting with the list it's recommended to use the
	 * delegate methods as they inform register PropertyChangeListener
	 * 
	 * @return processorsList
	 */
	@XmlElementWrapper(name = "dpu")		
	@XmlElement(name = "dataProcessor")			
	public List<DataProcessor> getProcessors() {
		if(processors == null)
			processors = new LinkedList<DataProcessor>();
		return processors;
	}
	
	public void setProcessors(List<DataProcessor> processors) {
		this.processors = processors;
	}
	
	/* Delegates for processor List*/
	
	public int size() {
		return processors.size();
	}

	public boolean isEmpty() {
		return processors.isEmpty();
	}

	public boolean contains(Object o) {
		return processors.contains(o);
	}

	public DataProcessor[] toArray() {
		DataProcessor[] returns = new DataProcessor[size()];
		return processors.toArray(returns);
	}

	public boolean add(DataProcessor e) {
		boolean add = processors.add(e);
		firePropertyChanged(null, e);
		return add;
	}

	public boolean remove(Object o) {
		boolean remove = processors.remove(o);
		firePropertyChanged();
		return remove;
	}

	public void clear() {
		processors.clear();
		firePropertyChanged();
	}

	public DataProcessor get(int index) {
		return processors.get(index);
	}

	public DataProcessor set(int index, DataProcessor element) {
		DataProcessor oldValue = processors.set(index, element);
		firePropertyChanged(oldValue, element);
		return oldValue;
	}

	public void add(int index, DataProcessor element) {
		processors.add(index, element);
		firePropertyChanged(null, element);
	}

	public DataProcessor remove(int index) {
		DataProcessor removedValue = processors.remove(index);
		firePropertyChanged(removedValue, null);
		return removedValue;
	}	
	
	
	/* Property Change Support */
	private final transient PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	protected void firePropertyChanged(Object oldValue, Object newValue) {
		support.firePropertyChange("processors", oldValue, newValue);
	}
	
	protected void firePropertyChanged() {
		firePropertyChanged(processors, processors);
	}
	
}
