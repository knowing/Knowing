package de.lmu.ifi.dbs.medmon.datamining.core.parameter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "parameter")
public class XMLParameterWrapper {

	@XmlAttribute(name = "key", required = true)
	private String key;
	
	@XmlAttribute(name = "value", required = true)
	private String value;

	@XmlAttribute(name = "type")
	private String type;
	
	public XMLParameterWrapper() {
		this("","","");
	}
		
	public XMLParameterWrapper(String key, String value, String type) {
		this.key = key;
		this.value = value;
		this.type = type;
	}

	public XMLParameterWrapper(IProcessorParameter parameter) {
		this.key = parameter.getName();
		this.type = parameter.getType();
		this.value = String.valueOf(parameter.getValue());
	}

	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "XMLParameterWrapper [key=" + key + ", value=" + value + ", type=" + type + "]";
	}
	
	

	
}
