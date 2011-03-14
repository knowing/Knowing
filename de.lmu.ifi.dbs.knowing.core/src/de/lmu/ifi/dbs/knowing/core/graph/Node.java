package de.lmu.ifi.dbs.knowing.core.graph;

import java.util.Properties;

import org.eclipse.core.runtime.ListenerList;

public abstract class Node implements INode {

	private final String factoryName;
	private final String factoryId;
	private final String nodeId;
	
	private final ListenerList nodeListeners = new ListenerList();
		
	protected boolean ready;
	protected Properties properties;
	protected GraphSupervisor supervisor;
		
	public Node(String factoryName, String factoryId, String nodeId) {
		this.factoryName = factoryName;
		this.factoryId = factoryId;
		this.nodeId = nodeId;
		initialize();
	}
		
	@Override
	public String getName() {
		return factoryName;
	}
	
	@Override
	public String getNodeId() {
		return nodeId;
	}
	
	@Override
	public String getFactoryId() {
		return factoryId;
	}
	
	@Override
	public boolean isReady() {
		return ready;
	}
	
	@Override
	public void setSupervisor(GraphSupervisor supervisor) {
		this.supervisor = supervisor;
		nodeListeners.add(supervisor);
	}
	
	@Override
	public void addNodeListener(INodeListener listener) {
		nodeListeners.add(listener);
	}
	
	@Override
	public void removeNodeListener(INodeListener listener) {
		nodeListeners.remove(listener);
	}
	
	public void fireNodeEvent(int type, Object serviceObject) {
		Object[] listeners = nodeListeners.getListeners();
		for (Object listener : listeners) {
			((INodeListener)listener).nodeChanged(new NodeEvent(type, this, serviceObject));			
		}
	}
	
	
	@Override
	public Properties getProperties() {
		return properties;
	}
	
	@Override
	public void setProperties(Properties properties) {
		if(validate(properties)) {
			this.properties = properties;
			initialize();
		}
		
	}
		
	abstract protected boolean validate(Properties properties);
	
	abstract public INode clone();
	
}
