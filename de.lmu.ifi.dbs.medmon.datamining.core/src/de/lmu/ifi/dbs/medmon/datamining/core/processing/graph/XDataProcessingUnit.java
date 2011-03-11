package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "xDataProcessingUnit")
@XmlAccessorType(XmlAccessType.FIELD)
public class XDataProcessingUnit {

	@XmlAttribute
	private String name;

	@XmlElement
	private String description;

	@XmlElement
	private String tags;

	@XmlJavaTypeAdapter(ProcessorNodeAdapter.class)
	@XmlElement(name = "processorNode")
	private Map<String, ProcessorNode> nodes;
	
	@XmlJavaTypeAdapter(InputNodeAdapter.class)
	@XmlElement(name = "inputNode")
	private Map<String, InputNode> inputNodes;

	@XmlElementWrapper(name = "edges")
	@XmlElement(name = "edge")
	private List<Edge> edges;

	public XDataProcessingUnit() {
		nodes = new HashMap<String, ProcessorNode>();
		inputNodes = new HashMap<String, InputNode>();
		edges = new ArrayList<Edge>();
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
	
	/* ==================== */
	/* ======= Edges ====== */
	/* ==================== */
	

	public List<Edge> getEdges() {
		return edges;
	}
	
	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	/* ==================== */
	/* == ProcessorNodes == */
	/* ==================== */
	
	public Map<String, ProcessorNode> getNodes() {
		return nodes;
	}

	protected void setNodes(Map<String, ProcessorNode> nodes) {
		this.nodes = nodes;
	}
	
	public Node addNode(Node node) {
		String nodeId = node.getNodeId();
		Node n = nodes.get(node.getNodeId());
		int i = 1;
		while (n != null) {
			node.setNodeId(nodeId + i++);
			n = nodes.get(node.getNodeId());
		}
		if(node instanceof InputNode)
			return inputNodes.put(node.getNodeId(), (InputNode) node);
		else if(node instanceof ProcessorNode)
			return nodes.put(node.getNodeId(), (ProcessorNode) node);
		return null;
	}
	
	/* ===================== */
	/* ==== Input Nodes ==== */
	/* ===================== */
	
	public void setInputNodes(Map<String, InputNode> inputNodes) {
		this.inputNodes = inputNodes;
	}

	public Map<String, InputNode> getInputNodes() {
		return inputNodes;
	}


	/* ====================== */
	/* == Processing Graph == */
	/* ====================== */

	public void buildGraph() {
		//TODO buildGraph() remove unused nodes
		//Link all Nodes	
/*		for (Edge edge : edges) {
			String sourceID = edge.getSourceID();
			String targetID = edge.getTargetID();
			ProcessorNode sourceNode = nodes.get(sourceID);
			ProcessorNode targetNode = nodes.get(targetID);
			
			edge.setSource(sourceNode);
			edge.setTarget(targetNode);
			
			sourceNode.getEdgesOut().add(edge);
			targetNode.getEdgesIn().add(edge);
			
			//targetNode.addProcessListener(sourceNode);
			sourceNode.addProcessListener(targetNode);
		}*/

	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[xDPU: name=");
		sb.append(name);
		sb.append("\n");
		
		for (Node node : nodes.values()) {
			sb.append("[node: ");
			sb.append(node);
			sb.append("\n");
		}
		
		return sb.toString();
	}

}
