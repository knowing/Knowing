package de.lmu.ifi.dbs.medmon.datamining.core.container;


public class RawData {

	private double[][] rawdata;
	private final int dimension;

	public RawData(int dimension) {
		this.dimension = dimension;
		rawdata = new double[dimension][];
	}

	public RawData(double[][] rawdata) {
		this.rawdata = rawdata;
		this.dimension = rawdata.length;
	}

	public void setDimension(int dimension, double[] data) {
		rawdata[dimension] = data;
	}

	public double[] getDimension(int dimension) {
		return rawdata[dimension];
	}

	public int getDimension() {
		return dimension;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RawData [dimension=");
		builder.append(dimension);
		builder.append("] RawData= ");
		builder.append("\n");
		for (int i = 0; i < rawdata[0].length; i++) {
			builder.append("{");
			for (int j = 0; j < dimension; j++) {
				builder.append(rawdata[j][i]);
				builder.append(",");
			}
			builder.delete(builder.length()-1, builder.length());
			builder.append("}");
			builder.append("\n");
		}
		return builder.toString();
	}

}
