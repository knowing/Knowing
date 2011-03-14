package de.lmu.ifi.dbs.knowing.core.graph.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.lmu.ifi.dbs.knowing.core.graph.Edge;
import de.lmu.ifi.dbs.knowing.core.graph.INode;

@XmlRootElement(name = "DataProcessingUnit")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataProcessingUnit {

	@XmlAttribute
	private String name;

	@XmlElement
	private String description;

	@XmlElement
	private String tags;
	
	@XmlElementWrapper(name = "nodes")
	@XmlElement(name = "node")
	private List<PersistentNode> nodes = new ArrayList<PersistentNode>();
	
	@XmlElementWrapper(name = "edges")
	@XmlElement(name = "edge")
	private List<Edge> edges = new ArrayList<Edge>();
	
	

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

	public List<PersistentNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<PersistentNode> nodes) {
		this.nodes = nodes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	public boolean addEdge(Edge e) {
		return edges.add(e);
	}

	public boolean add(INode node) {
		if(node instanceof PersistentNode)
			return nodes.add((PersistentNode) node);
		else
			return nodes.add(new PersistentNode(node));
	}
	
	
	
	
}
