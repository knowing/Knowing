package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;

//This statement means that class "DataProcessingUnit.java" is the root-element 
//(namespace = "de.lmu.ifi.dbs.medmon.datamining.core.processing")
@XmlRootElement(name = "DataProcessingUnit")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataProcessingUnit {

	@XmlElement
	private String name = "default";

	@XmlElement
	private String description;

	@XmlElementWrapper(name = "dpu")
	@XmlElement(name = "dataProcessor")
	private List<XMLDataProcessor> processors;

	@XmlElementWrapper(name = "clusters")
	@XmlElement(name = "cluster")
	private List<ClusterUnit> embeddedClusters;

	private transient Map<String, ClusterUnit> clusters;

	public DataProcessingUnit() {
		processors = new LinkedList<XMLDataProcessor>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * For interacting with the list it's recommended to use the delegate
	 * methods as they inform register PropertyChangeListener
	 * 
	 * @return processorsList
	 */
	public List<XMLDataProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<XMLDataProcessor> processors) {
		firePropertyChanged(this.processors, processors);
		this.processors = processors;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ClusterUnit> getEmbeddedClusters() {
		return embeddedClusters;
	}

	protected void setEmbeddedClusters(List<ClusterUnit> embeddedClusters) {
		firePropertyChanged(this.embeddedClusters, embeddedClusters);
		this.embeddedClusters = embeddedClusters;
	}

	public void setClusters(Map<String, ClusterUnit> clusters) {
		this.clusters = clusters;
		embeddedClusters.clear();
		for (ClusterUnit unit : clusters.values())
			add(unit);
	}

	public Map<String, ClusterUnit> getClusters() {
		return clusters;
	}

	public boolean putCluster(ClusterUnit unit) {
		if (unit.getName() == null || unit.getName().isEmpty())
			return false;

		HashMap<String, ClusterUnit> old = new HashMap<String, ClusterUnit>(clusters);
		clusters.put(unit.getName(), unit);
		firePropertyChanged(old, clusters);
		return true;
	}
	
	public void removeCluster(String name) {
		HashMap<String, ClusterUnit> old = new HashMap<String, ClusterUnit>(clusters);
		clusters.remove(name);
		firePropertyChanged(old, clusters);
	}

	/* Delegates for processor List */

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

	/* Delegates for embeddedCluster */

	public boolean add(ClusterUnit e) {
		boolean add = embeddedClusters.add(e);
		if (add)
			firePropertyChanged(null, e);
		return add;
	}

	@Override
	public String toString() {
		return name;
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

}
