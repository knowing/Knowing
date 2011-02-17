package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessorNode {

	private transient List<Edge> edgesIn = new ArrayList<Edge>();
	private transient List<Edge> edgesOut = new ArrayList<Edge>();

	@XmlElement
	private XMLDataProcessor processor;

	@XmlAttribute
	private String nodeId;

	private transient int id;

	protected ProcessorNode() {
	}

	public ProcessorNode(XMLDataProcessor processor, int id) {
		this.processor = processor;
		this.id = id;
		this.nodeId = processor.getId() + id;
	}

	public List<Edge> getEdgesIn() {
		return edgesIn;
	}

	public void setEdgesIn(List<Edge> edgesIn) {
		this.edgesIn = edgesIn;
	}

	public List<Edge> getEdgesOut() {
		return edgesOut;
	}

	public void setEdgesOut(List<Edge> edgesOut) {
		this.edgesOut = edgesOut;
	}

	public XMLDataProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(XMLDataProcessor processor) {
		this.processor = processor;
		nodeId = processor.getId() + id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getNodeId() {
		return nodeId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessorNode ");
		builder.append("[nodeId=");
		builder.append(nodeId);
		builder.append(", \n edgesIn=");
		builder.append(edgesIn);
		builder.append(", \n edgesOut=");
		builder.append(edgesOut);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	

}
