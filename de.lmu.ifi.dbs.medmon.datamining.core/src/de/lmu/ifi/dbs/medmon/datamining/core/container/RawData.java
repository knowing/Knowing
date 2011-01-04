package de.lmu.ifi.dbs.medmon.datamining.core.container;

/**
 * Representing data that is being processed.
 * 
 * @author Alexander Stautner,Nepomuk Seiler
 * @version 1.1
 */
public class RawData {

	private double[][] rawdata;
	private long[] timestamp;
	private final int dimension;
	private String label;

	public RawData(int dimension) {
		this(new double[dimension][], new long[0], dimension);
	}

	public RawData(double[][] rawdata) {
		this(rawdata, new long[rawdata[0].length], rawdata.length);
	}
	
	public RawData(double[][] rawdata, long[] timestamp, int dimension) {
		this("",rawdata, timestamp, dimension);
	}
	
	public RawData(String label, double[][] rawdata, long[] timestamp, int dimension) {
		this.rawdata = rawdata;
		this.timestamp = timestamp;
		this.dimension = dimension;
		this.label = label;
	}

	public void setDimension(int dimension, double[] data) {
		rawdata[dimension] = data;
		if(timestamp.length != data.length)
			timestamp = new long[data.length];
	}

	public double[] getDimension(int dimension) {
		return rawdata[dimension];
	}
	
	public void setTimestamp(long[] timestamp) {
		this.timestamp = timestamp;
	}
	
	public long[] getTimestamp() {
		return timestamp;
	}
	
	public int size() {
		return rawdata[0].length;
	}
	
	public int dimension() {
		return dimension;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RawData [dimension=");
		builder.append(dimension);
		builder.append(", label=");
		builder.append(label);
		builder.append("]");
		return builder.toString();
	}



}
