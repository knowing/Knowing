package de.lmu.ifi.dbs.medmon.medic.core.unit;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicProcessingUnit {

	@XmlElementWrapper(name = "mpu")		
	@XmlElement(name = "dpu")	
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
}
