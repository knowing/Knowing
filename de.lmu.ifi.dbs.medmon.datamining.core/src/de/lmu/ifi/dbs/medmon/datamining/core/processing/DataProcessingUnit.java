package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

//This statement means that class "DataProcessingUnit.java" is the root-element 
@XmlRootElement(namespace = "de.lmu.ifi.dbs.medmon.datamining.core.processing")
public class DataProcessingUnit{
	
	private List<DataProcessor> processors;
	
	private String name = "default";
	
	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElementWrapper(name = "dpu")		
	@XmlElement(name = "dataProcessor")			
	public List<DataProcessor> getProcessors() {
		return processors;
	}
	
	public void setProcessors(List<DataProcessor> processors) {
		this.processors = processors;
	}
		

}
