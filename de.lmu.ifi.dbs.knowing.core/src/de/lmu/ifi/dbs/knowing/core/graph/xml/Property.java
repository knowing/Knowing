package de.lmu.ifi.dbs.knowing.core.graph.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class Property {
	
	//@XmlAttribute
	private String key;

	//@XmlAttribute
	private String value;

	public Property() {
		this("","");
	}
	
	public Property(String key) {
		this(key, "");
	}

	public Property(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	@XmlAttribute
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
