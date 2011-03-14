package de.lmu.ifi.dbs.knowing.core.graph.xml;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.lmu.ifi.dbs.knowing.core.factory.ILoaderFactory;
import de.lmu.ifi.dbs.knowing.core.factory.IProcessorFactory;
import de.lmu.ifi.dbs.knowing.core.graph.GraphSupervisor;
import de.lmu.ifi.dbs.knowing.core.graph.INode;
import de.lmu.ifi.dbs.knowing.core.graph.INodeListener;
import de.lmu.ifi.dbs.knowing.core.graph.InputNode;
import de.lmu.ifi.dbs.knowing.core.graph.NodeEvent;
import de.lmu.ifi.dbs.knowing.core.graph.ProcessorNode;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PersistentNode implements INode {

	public static final String INPUT_NODE = "input";
	public static final String PROCESSOR_NODE = "processor";
		
	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String nodeId;
	
	@XmlAttribute
	private String factoryId;
	
	@XmlAttribute
	private String type;
	
	@XmlJavaTypeAdapter(PropertiesAdapter.class)
	private Properties properties;
	
	private transient INode node;
	
	protected PersistentNode() { }
	
	/**
	 * 
	 * @param node - cannot be null
	 */
	public PersistentNode(INode node) {
		this.node = node;
		name = node.getName();
		nodeId = node.getNodeId();
		factoryId = node.getFactoryId();
		properties = node.getProperties();
		type = type(node);		
	}
	
	/**
	 * 
	 * @param type - {@link PersistentNode#INPUT_NODE}, {@link PersistentNode#PROCESSOR_NODE}
	 * @param nodeId 
	 * @param factoryId - {@link IProcessorFactory} or {@link ILoaderFactory} must be registered with this id
	 * @param properties
	 */
	public PersistentNode(String type, String nodeId, String factoryId, Properties properties) {
		this.type = type;
		this.nodeId = nodeId;
		this.factoryId = factoryId;
		this.properties = properties;
		this.node = getNode();
	}
	
	public void update() {
		if(type.equals(PROCESSOR_NODE)) {
			node = new ProcessorNode(name, factoryId, nodeId);
			node.setProperties(properties);
		} else if (type.equals(INPUT_NODE)) {
			node = new InputNode(name, factoryId, nodeId);
			node.setProperties(properties);
		}
	}

	/**
	 * This method calls the update method before returning <br>
	 * an INode Object.  
	 * @return new INode instance
	 */
	public INode getNode() {
		update();
		return node;
	}
	
	private String type(INode node) {
		if(node instanceof ProcessorNode)
			return PROCESSOR_NODE;
		else if (node instanceof InputNode)
			return INPUT_NODE;
		else
			return "unkown";
	}

	@Override
	public void nodeChanged(NodeEvent event) {
		node.nodeChanged(event);
	}

	@Override
	public void run() throws Exception {
		node.run();
	}

	@Override
	public boolean isReady() {
		return node.isReady();
	}

	@Override
	public void initialize() {
		node.initialize();
	}

	@Override
	public void setSupervisor(GraphSupervisor supervisor) {
		node.setSupervisor(supervisor);
	}

	@Override
	public void addNodeListener(INodeListener listener) {
		node.addNodeListener(listener);
	}

	@Override
	public void removeNodeListener(INodeListener listener) {
		node.removeNodeListener(listener);
	}

	@Override
	public String getName() {
		return name;
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
	public Properties getProperties() {
		return node.getProperties();
	}

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	@Override
	public INode clone() {
		return new PersistentNode(getNode().clone());
	}

}
