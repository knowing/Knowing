package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Edge {

	private transient ProcessorNode source;
	private transient ProcessorNode target;
	
	@XmlAttribute
	private String sourceID;
	
	@XmlAttribute
	private String targetID;

	protected Edge() { }
	
	public Edge(ProcessorNode source, ProcessorNode target) {
		this.source = source;
		this.target = target;
		this.sourceID = source.getNodeId();
		this.targetID = target.getNodeId();
	}

	public ProcessorNode getSource() {
		return source;
	}

	public void setSource(ProcessorNode source) {
		this.source = source;
	}

	public ProcessorNode getTarget() {
		return target;
	}

	public void setTarget(ProcessorNode target) {
		this.target = target;
	}

	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public String getTargetID() {
		return targetID;
	}

	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Edge [sourceID=");
		builder.append(sourceID);
		builder.append(", targetID=");
		builder.append(targetID);
		builder.append("]");
		return builder.toString();
	}
	
	

}
