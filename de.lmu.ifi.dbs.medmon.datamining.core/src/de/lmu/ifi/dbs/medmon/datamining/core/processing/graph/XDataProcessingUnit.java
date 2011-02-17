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

	@XmlJavaTypeAdapter(NodeAdapter.class)
	@XmlElement(name = "processorNode")
	private Map<String, ProcessorNode> nodes;

	@XmlElementWrapper(name = "edges")
	@XmlElement(name = "edge")
	private List<Edge> edges;

	public XDataProcessingUnit() {
		nodes = new HashMap<String, ProcessorNode>();
		edges = new ArrayList<Edge>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, ProcessorNode> getNodes() {
		return nodes;
	}

	protected void setNodes(Map<String, ProcessorNode> nodes) {
		this.nodes = nodes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public ProcessorNode addNode(ProcessorNode node) {
		int id = node.getId();
		ProcessorNode n = nodes.get(node.getNodeId());
		while (n != null) {
			node.setId(id++);
			n = nodes.get(node.getNodeId());
		}
		node.setId(id++);
		return nodes.put(node.getNodeId(), node);
	}

	public void addNodes(List<ProcessorNode> nodes) {
		for (ProcessorNode node : nodes)
			addNode(node);
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
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

	/* ====================== */
	/* == Processing Graph == */
	/* ====================== */

	public void buildGraph() {
		//TODO buildGraph() remove unused nodes
		//Go through all nodes
		for (ProcessorNode node : nodes.values()) {
			String id = node.getNodeId();
			//Go through all edges
			for (Edge edge : edges) {
				if (edge.getSourceID().equals(id)) {
					//Connect Node and outgoing edge
					node.getEdgesOut().add(edge);
					edge.setSource(node);
				} else if (edge.getTargetID().equals(id)) {
					//Connect Node and incoming edge
					node.getEdgesIn().add(edge);
					edge.setTarget(node);
				}				
			}
		}
		
		System.out.println("Graph builded");

	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[xDPU: name=");
		sb.append(name);
		sb.append("\n");
		
		for (ProcessorNode node : nodes.values()) {
			sb.append("[node: ");
			sb.append(node);
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
