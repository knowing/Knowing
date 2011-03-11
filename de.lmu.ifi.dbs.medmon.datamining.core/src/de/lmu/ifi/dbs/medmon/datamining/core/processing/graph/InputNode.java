package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import java.net.URL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import weka.core.converters.Loader;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InputNode extends Node{

	@XmlAttribute
	private String name;
	
	@XmlElement
	private URL url;
	
	private Loader loader;

	
	protected InputNode() {
		
	}

	public InputNode(String name, URL url) {
		this.name = name;
		this.url = url;
		setNodeId(name);
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	@Override
	public Object getData() {
		return url;
	}
	
	
	
}
