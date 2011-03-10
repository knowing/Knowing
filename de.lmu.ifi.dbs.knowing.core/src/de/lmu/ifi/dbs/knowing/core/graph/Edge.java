package de.lmu.ifi.dbs.knowing.core.graph;

public class Edge {

	private final String id;
	private INode source;
	private INode target;
	
	private String sourceId;
	private String targetId;
	
	private int weight;
	
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
	public String toString() {
		return source + " to (" + weight + ")" + target;
	}
	
	
}
