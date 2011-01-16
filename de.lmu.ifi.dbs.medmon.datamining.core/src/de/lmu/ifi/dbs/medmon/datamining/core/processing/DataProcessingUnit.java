package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.beans.PropertyChangeEvent;
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
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;

//This statement means that class "DataProcessingUnit.java" is the root-element 
//(namespace = "de.lmu.ifi.dbs.medmon.datamining.core.processing")
@XmlRootElement(name = "DataProcessingUnit")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataProcessingUnit {

	@XmlElement
	private String name;

	@XmlElement
	private String description;

	@XmlElement
	private String tags;

	@XmlElementWrapper(name = "dpu")
	@XmlElement(name = "dataProcessor")
	private List<XMLDataProcessor> processors;

	@XmlElementWrapper(name = "clusters")
	@XmlElement(name = "cluster")
	private List<ClusterUnit> embeddedClusters;

	private transient Map<String, ClusterUnit> clusters;
	private transient String file;

	public DataProcessingUnit() {
		processors = new LinkedList<XMLDataProcessor>();
	}
	
	public DataProcessingUnit(String file) {
		this();
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTags() {
		return tags;
	}
	
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public void addTag(String tag) {
		tags += "," + tag;
	}
	
	/**
	 * For interacting with the list it's recommended to use the delegate
	 * methods as they inform registered PropertyChangeListener
	 * 
	 * @return processorsList
	 */
	public List<XMLDataProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<XMLDataProcessor> processors) {
		firePropertyChanged(getProcessors(), processors);
		for (XMLDataProcessor processor : getProcessors())
			deregisterParameterListener(processor);

		for (XMLDataProcessor processor : processors)
			registerParameterListener(processor);
		this.processors = processors;
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

	public void initParameterListener() {
		for (XMLDataProcessor processor : processors)
			registerParameterListener(processor);
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
		if (add)
			registerParameterListener(e);
		firePropertyChanged(null, e);
		return add;
	}

	public boolean remove(Object o) {
		boolean remove = processors.remove(o);
		if (remove)
			deregisterParameterListener((XMLDataProcessor) o);

		firePropertyChanged(o, null);
		return remove;
	}

	public void clear() {
		for (XMLDataProcessor processor : processors)
			deregisterParameterListener(processor);

		processors.clear();
		firePropertyChanged(null, processors);
	}

	public XMLDataProcessor get(int index) {
		return processors.get(index);
	}

	public XMLDataProcessor set(int index, XMLDataProcessor element) {
		XMLDataProcessor oldValue = processors.set(index, element);
		deregisterParameterListener(oldValue);
		registerParameterListener(element);
		firePropertyChanged(oldValue, element);
		return oldValue;
	}

	public void add(int index, XMLDataProcessor element) {
		processors.add(index, element);
		firePropertyChanged(null, element);
	}

	public XMLDataProcessor remove(int index) {
		XMLDataProcessor removedValue = processors.remove(index);
		registerParameterListener(removedValue);
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
	
	public String getFile() {
		return file;
	}
	
	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return name;
	}

	/* Property Change Support */

	private final transient PropertyChangeSupport support = new PropertyChangeSupport(this);

	/**
	 * This listener is registered on every {@link IProcessorParameter}
	 */
	private final transient PropertyChangeListener parameterListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			support.firePropertyChange(evt);
		}
	};

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	protected void firePropertyChanged(Object oldValue, Object newValue) {
		support.firePropertyChange("processors", oldValue, newValue);
	}

	private void registerParameterListener(XMLDataProcessor processor) {
		if (processor == null)
			return;
		for (IProcessorParameter parameter : processor.getParameters().values()) {
			parameter.addPropertyChangeListener(parameterListener);
		}
	}

	private void deregisterParameterListener(XMLDataProcessor processor) {
		if (processor == null)
			return;
		for (IProcessorParameter parameter : processor.getParameters().values()) {
			parameter.removePropertyChangeListener(parameterListener);
		}
	}

}
