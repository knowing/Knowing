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
@XmlRootElement(name = "DataProcessingUnit")
public class DataProcessingUnit{
	
	private List<XMLDataProcessor> processors;
	
	private String name = "default";
	
	public DataProcessingUnit() {
		processors = new LinkedList<XMLDataProcessor>();
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
	public List<XMLDataProcessor> getProcessors() {
		if(processors == null)
			processors = new LinkedList<XMLDataProcessor>();
		return processors;
	}
	
	public void setProcessors(List<XMLDataProcessor> processors) {
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

	public XMLDataProcessor[] toArray() {
		XMLDataProcessor[] returns = new XMLDataProcessor[size()];
		return processors.toArray(returns);
	}

	public boolean add(XMLDataProcessor e) {
		boolean add = processors.add(e);
		firePropertyChanged(null, e);
		return add;
	}

	public boolean remove(Object o) {
		boolean remove = processors.remove(o);
		firePropertyChanged(o, null);
		return remove;
	}

	public void clear() {
		processors.clear();
		firePropertyChanged(null, processors);
	}

	public XMLDataProcessor get(int index) {
		return processors.get(index);
	}

	public XMLDataProcessor set(int index, XMLDataProcessor element) {
		XMLDataProcessor oldValue = processors.set(index, element);
		firePropertyChanged(oldValue, element);
		return oldValue;
	}

	public void add(int index, XMLDataProcessor element) {
		processors.add(index, element);
		firePropertyChanged(null, element);
	}

	public XMLDataProcessor remove(int index) {
		XMLDataProcessor removedValue = processors.remove(index);
		firePropertyChanged(removedValue, null);
		return removedValue;
	}	
	
	@Override
	public String toString() {
		return name;
	}
	
	//TODO Use Eclipse PropertyChange Support with ListenerList
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
		
}
