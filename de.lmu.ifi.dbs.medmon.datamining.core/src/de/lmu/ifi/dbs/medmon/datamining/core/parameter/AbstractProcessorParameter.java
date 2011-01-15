package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractProcessorParameter<E> implements IProcessorParameter<E> {

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);
	private final String name;
	private final String type;
		
	public AbstractProcessorParameter(String name, String type) {
		this.name = name;
		this.type = type;
	}
		
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getType() {
		return type;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	};
	
	protected void fireParameterChanged(String parameter, Object oldValue, Object newValue) {
		PropertyChangeEvent event = new PropertyChangeEvent(this, "parameter." + parameter, oldValue, newValue);
		support.firePropertyChange(event);
	}
}
