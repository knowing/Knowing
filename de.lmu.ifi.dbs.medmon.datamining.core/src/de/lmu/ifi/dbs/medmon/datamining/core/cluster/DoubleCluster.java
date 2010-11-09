package de.lmu.ifi.dbs.medmon.datamining.core.cluster;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "cluster")
@XmlType
public class DoubleCluster {

	private List<Double> centroid;
	private String label;
	
	public DoubleCluster() {}
	
	public DoubleCluster(String label, double[] centroid) {
		this.label = label;
		this.centroid = new ArrayList<Double>();
		for (double d : centroid) 
			this.centroid.add(d);
	}

	@XmlElementWrapper(name = "centroid")		
	@XmlElement(name = "double")			
	protected List<Double> getCentroid() {
		return centroid;
	}

	protected void setCentroid(List<Double> centroid) {
		this.centroid = centroid;
	}
	
	public double[] getCentroidArray() {
		double[] returns = new double[centroid.size()];
		int index = 0;
		for (Double d : centroid)
			returns[index++] = d;
		return returns;
	}

	protected String getLabel() {
		return label;
	}

	protected void setLabel(String label) {
		this.label = label;
	}
	
}
