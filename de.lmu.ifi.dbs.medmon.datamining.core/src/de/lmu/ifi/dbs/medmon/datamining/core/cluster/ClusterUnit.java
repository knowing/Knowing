package de.lmu.ifi.dbs.medmon.datamining.core.cluster;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "de.lmu.ifi.dbs.medmon.datamining.core.processing")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterUnit {

	@XmlElementWrapper(name = "clusterList")
	@XmlElement(name = "cluster")
	private List<DoubleCluster> clusterlist = new LinkedList<DoubleCluster>();

	@XmlAttribute(required = true)
	private String name;
	
	@XmlElement
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DoubleCluster> getClusterlist() {
		return clusterlist;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setClusterlist(List<DoubleCluster> clusterlist) {
		this.clusterlist = clusterlist;
	}

	public void addCluster(DoubleCluster cluster) {
		clusterlist.add(cluster);
	}


}
