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

	public RawData(int dimension) {
		this(new double[dimension][], new long[0], dimension);
	}

	public RawData(double[][] rawdata) {
		this(rawdata, new long[rawdata[0].length], rawdata.length);
	}
	
	public RawData(double[][] rawdata, long[] timestamp, int dimension) {
		this.rawdata = rawdata;
		this.timestamp = timestamp;
		this.dimension = dimension;
	}

	public void setDimension(int dimension, double[] data) {
		rawdata[dimension] = data;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RawData [dimension=");
		builder.append(dimension);
		builder.append("] RawData= ");
		builder.append("\n");
		for (int i = 0; i < 10; i++) {
			builder.append("{");
			for (int j = 0; j < dimension; j++) {
				builder.append(rawdata[j][i]);
				builder.append(" / ");
				builder.append(timestamp[i]);
				builder.append(",");
			}
			builder.delete(builder.length()-1, builder.length());
			builder.append("}");
			builder.append("\n");
		}
		return builder.toString();
	}

}
