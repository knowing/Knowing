package de.lmu.ifi.dbs.knowing.core.graph;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Edge {
	
	private transient INode source;
	private transient INode target;
	
	@XmlAttribute
	private String id;
	
	@XmlAttribute
	private String sourceId;
	
	@XmlAttribute
	private String targetId;
	
	@XmlAttribute
	private int weight;
	
	protected Edge() {
		this.id = String.valueOf(System.currentTimeMillis());
	}
	
	public Edge(String id,INode source, INode target) {
		this(id,source, target, 1);
	}
	
	public Edge(String id, String sourceId, String targetId) {
		this(id,sourceId, targetId, 1);
	}
	
	public Edge(String id, INode source, INode target, int weight) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.weight = weight;
	}
		
	public Edge(String id, String sourceId, String targetId, int weight) {
		this.id = id;
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.weight = weight;
	}

	public String getId() {
		return id;
	}
	
	public INode getSource() {
		return source;
	}

	public void setSource(INode source) {
		this.source = source;
	}

	public INode getTarget() {
		return target;
	}

	public void setTarget(INode target) {
		this.target = target;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
		this.source = null;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
		this.target = null;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	@Override
	public Edge clone() {
		if(source == null || target == null)
			return new Edge(id, sourceId, targetId, weight);
		else
			return new Edge(id, source.clone(), target.clone());
	}

	@Override
	public String toString() {
		if(source == null && target == null)
			return sourceId + " to (" + weight + ")" + targetId;
		return source + " to (" + weight + ")" + target;
	}
	
	
}
