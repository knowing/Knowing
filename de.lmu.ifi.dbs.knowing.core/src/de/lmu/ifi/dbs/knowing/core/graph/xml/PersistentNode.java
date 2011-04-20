package de.lmu.ifi.dbs.knowing.core.graph.xml;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.lmu.ifi.dbs.knowing.core.factory.ILoaderFactory;
import de.lmu.ifi.dbs.knowing.core.factory.IProcessorFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PersistentNode {

	public static final String INPUT_NODE = "input";
	public static final String PROCESSOR_NODE = "processor";
	public static final String PRESENTER_NODE = "presenter";
		
	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String nodeId;
	
	@XmlAttribute
	private String type;
	
	@XmlElement
	private String factoryId;
		
//	@XmlJavaTypeAdapter(PropertiesAdapter.class)
	private Properties properties;
	
	
	protected PersistentNode() { }
		
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
	}
	
//	public void update() {
//		if(type.equals(PROCESSOR_NODE)) {
//			node = new ProcessorNode(name, factoryId, nodeId);
//			node.setProperties(properties);
//		} else if (type.equals(INPUT_NODE)) {
//			node = new InputNode(name, factoryId, nodeId);
//			node.setProperties(properties);
//		} else if (type.equals(PRESENTER_NODE)) {
//			node = new PresenterNode(name, factoryId, nodeId);
//			node.setProperties(properties);
//		}
//	}
//
//	/**
//	 * To get a fresh node, call #update and then getNode.
//	 * @return
//	 */
//	public INode getNode() {
//		if(node == null)
//			update();
//		return node;
//	}
//	
//	private String type(INode node) {
//		if(node instanceof ProcessorNode)
//			return PROCESSOR_NODE;
//		else if (node instanceof InputNode)
//			return INPUT_NODE;
//		else if (node instanceof PresenterNode)
//			return PRESENTER_NODE;
//		else
//			return "unkown";
//	}
	
	public String getFactoryId() {
		return factoryId;
	}

}
