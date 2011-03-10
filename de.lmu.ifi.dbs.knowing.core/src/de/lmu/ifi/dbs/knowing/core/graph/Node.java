package de.lmu.ifi.dbs.knowing.core.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	public void loadProperties(InputStream in) throws IOException {
		Properties newProperties = new Properties();
		newProperties.load(in);
		if(validate(newProperties))
			properties = newProperties;
		initialize();
		
	}
	
	abstract protected boolean validate(Properties properties);
	
	
}
