package de.lmu.ifi.dbs.medmon.datamining.core.cluster;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "cluster")
@XmlType
public class DoubleCluster extends AbstractCluster<Double>{

	private List<Double> values;
	private int numChildren;
	

	public DoubleCluster() {
		super("unkown");
	}
	
	public DoubleCluster(String label, double[] centroid) {
		this(label, centroid, 0);
	}
	

	public DoubleCluster(String label,double[] centroid, int numChildren) {
		super(label);
		this.numChildren = numChildren;
		this.values = new LinkedList<Double>();
		for (double d : centroid) 
			this.values.add(d);
	}
	
	@XmlElementWrapper(name = "centroid")		
	@XmlElement(name = "double")	
	@Override
	public List<Double> getValues() {
		return values;
	}
	
	@Override
	public ClusterType getType() {
		return ClusterType.CENTROID;
	}
	
	@XmlAttribute
	public String getLabel() {
		return super.getLabel();
	}

	protected void setLabel(String label) {
		this.label = label;
	}
	
	public double[] getCentroidArray() {
		double[] returns = new double[values.size()];
		int index = 0;
		for (Double d : values)
			returns[index++] = d;
		return returns;
	}
	
	public int getNumChildren() {
		return numChildren;
	}
	
}
