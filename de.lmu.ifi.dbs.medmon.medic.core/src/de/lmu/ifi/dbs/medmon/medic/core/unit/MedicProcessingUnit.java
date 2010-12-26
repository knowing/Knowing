package de.lmu.ifi.dbs.medmon.medic.core.unit;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicProcessingUnit {
	
	@XmlAttribute
	private String name;
	
	@XmlElement
	private String description;
	
	@XmlElementWrapper
	@XmlElement(name = "DataProcessingUnit")
	private List<DataProcessingUnit> dpus;
	
	public MedicProcessingUnit() {
		dpus = new LinkedList<DataProcessingUnit>();
	}
	
	public List<DataProcessingUnit> getDpus() {
		return dpus;
	}
	
	public void setDpus(List<DataProcessingUnit> dpus) {
		this.dpus = dpus;
	}

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
	
	@Override
	public String toString() {
		return name;
	}
}
