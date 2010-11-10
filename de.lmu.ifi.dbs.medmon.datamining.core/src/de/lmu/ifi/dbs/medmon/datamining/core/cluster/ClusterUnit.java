package de.lmu.ifi.dbs.medmon.datamining.core.cluster;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "de.lmu.ifi.dbs.medmon.datamining.core.processing")
public class ClusterUnit {

	private List<DoubleCluster> clusterlist;

	private String name;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElementWrapper(name = "clusterList")
	@XmlElement(name = "cluster")
	public List<DoubleCluster> getClusterlist() {
		return clusterlist;
	}

	public void setClusterlist(List<DoubleCluster> clusterlist) {
		this.clusterlist = clusterlist;
	}

	public void addCluster(DoubleCluster cluster) {
		clusterlist.add(cluster);
	}
}
