package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Node implements INode {

	@XmlAttribute
	private String nodeId;
		
	protected transient List<Edge> edgesIn = new ArrayList<Edge>();
	protected transient List<Edge> edgesOut = new ArrayList<Edge>();
	
	public Node() {	}
	
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	@Override
	public String getNodeId() {
		return nodeId;
	}

	@Override
	public List<Edge> getEdgesIn() {
		return edgesIn;
	}

	@Override
	public List<Edge> getEdgesOut() {
		return edgesOut;
	}
		
	public abstract String getName();

	public abstract Object getData();


}
