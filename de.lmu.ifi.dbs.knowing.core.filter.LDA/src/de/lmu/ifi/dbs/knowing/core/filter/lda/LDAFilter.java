package de.lmu.ifi.dbs.knowing.core.filter.lda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.SimpleBatchFilter;

public class LDAFilter extends SimpleBatchFilter{
	
	private final static double DIMENSION_REDUCTION_PER = 0.95;
	private static double SINGULARITY_DETECTION_THRESHOLD = 1.0E-60;
	private static final long serialVersionUID = 1L;
	private ArrayList<String> classList;
	private int dimension;
	private double[] totalMean;
	private double[][] groupMean;
	private Matrix eigenVectorMatrix;
	private double[] eigenValues;
	
	@Override
	public String globalInfo() {
		// TODO change description
		return "A simple batch filter that adds an additional attribute 'bla' at the end containing the index of the processed instance.";
	}
	
	@Override
	protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
		Instances result = new Instances(inputFormat, 0);
		return result;
	}
	@Override
	public Capabilities getCapabilities() {
	     Capabilities result = super.getCapabilities();	     
	     result.enable(Capability.NUMERIC_ATTRIBUTES); // only numeric attributes are accepted
	     result.enable(Capability.STRING_CLASS);  // filter needs a class to be set
	     return result;
	}
	
	
		
	protected Instances process(Instances inst) throws Exception {
		
		this.performLDA(inst);
		Instances result = new Instances(determineOutputFormat(inst), 0);
	    for (int i = 0; i < inst.numInstances(); i++) {
	    	Instance r = transformData(inst.get(i));
	    	result.add(r);
	    }
		return result;
	}
	
	private void performLDA(Instances inst) throws Exception{
		
		dimension = inst.numAttributes()-1;
		classList = Collections.list((Enumeration<String>)inst.classAttribute().enumerateValues());								
		
		// divide data into subsets
		ArrayList<ArrayList<ArrayList<Double>>> subset = new ArrayList<ArrayList<ArrayList<Double>>>();
		for (int i = 0; i < classList.size(); i++) {
			String currentClasss = classList.get(i);
			ArrayList<ArrayList<Double>> si = new ArrayList<ArrayList<Double>>();			
			for (int j = 0; j < inst.numInstances(); j++) {		
				Instance in = inst.get(j);
				String cla = inst.classAttribute().value((int)(in.value(inst.classAttribute())));
				if(cla.equals(currentClasss)){
					ArrayList<Double> al = new ArrayList<Double>();
					for(int k=0;k<in.numAttributes();k++){
						if(k!=inst.classIndex()){
							al.add(in.value(k));
						}
					}
					si.add(al);
				}
			}
			subset.add(i, si);
		}
		
		// calculate group mean
		groupMean = new double[subset.size()][dimension];
		for (int i = 0; i < groupMean.length; i++) {
			for (int j = 0; j < groupMean[i].length; j++) {
				groupMean[i][j] = getGroupMean(j, subset.get(i));
			}
		}

		// calculate total mean
		totalMean = new double[dimension];
		for (int i = 0; i < totalMean.length; i++) {
			totalMean[i] = getTotalMean(i, inst);
		}
		
		// calculate covariance matrices
		double[][][] covariance = new double[subset.size()][dimension][dimension];
		for (int i = 0; i < subset.size(); i++){
			covariance[i] = getCovarianceMatrix(subset.get(i), groupMean[i]);
		}
		
		//test for matrix singularity
		for(int i=0;i<covariance.length;i++){
			Matrix covM = new Matrix(covariance[i]);
			double det = covM.det();			
			if(det < SINGULARITY_DETECTION_THRESHOLD || Double.valueOf(det).isNaN()){
				throw new Exception("matrix got singular...");
			}
		}
		
		//calculate the within-class scatter matrix
		double[][] sw = new double[dimension][dimension];
		for (int i = 0; i < covariance.length; i++){
			for(int j=0; j <  covariance[i].length; j++){
				for(int k=0; k < covariance[i][j].length; k++){
					sw[j][k] += covariance[i][j][k];
				}
			}			
		}
		
		//calculate the between-class scatter matrix
		double[][] sb = new double[dimension][dimension];
		for(int i=0; i < subset.size();i++){			
			for(int j=0;j<dimension;j++){
				for(int k=0;k<dimension;k++){
					//sb[j][k] += subset[i].size() * (groupMean[i][j]-totalMean[j])*(groupMean[i][k]-totalMean[k]);
					sb[j][k] += dimension * (groupMean[i][j]-totalMean[j])*(groupMean[i][k]-totalMean[k]);
				}
			}
		}
		
		Matrix sbm = new Matrix(sb);
		Matrix swm = new Matrix(sw);
		
		
		Matrix criterion = (swm.inverse()).times(sbm);
		EigenvalueDecomposition evd = new EigenvalueDecomposition(criterion);
		eigenVectorMatrix = evd.getV();		
		eigenValues = evd.getRealEigenvalues();
		
		this.sortEigenvalues();
	}
	
	private void sortEigenvalues(){
		double[] evArray = Arrays.copyOf(eigenValues, eigenValues.length);
		double[] evSorted = new double[evArray.length];
		
		HashMap<Double, Vector<Integer>> evMap = new HashMap<Double,Vector<Integer>>();
		for(int i=0;i<evArray.length;i++){
			Vector<Integer> v = evMap.get(evArray[i]);
			if(v==null){
				v = new Vector<Integer>();
			}
			v.add(i);
			evMap.put(evArray[i], v);			
		}
		ArrayList<Double> evList = new ArrayList<Double>(evMap.keySet());
		Collections.sort(evList,Collections.reverseOrder());
		
		
		double[] normFactor = new double[dimension];
		for(int i=0;i<dimension;i++){
			double absMax = 0.0;
			for(int j=0;j<dimension;j++){
				if(Math.abs(eigenVectorMatrix.get(j, i))>absMax){
					absMax = Math.abs(eigenVectorMatrix.get(j, i));
				}
			}
			normFactor[i] = absMax;
		}		
				
		double[][] temp = eigenVectorMatrix.getArrayCopy();
		int newIndex = 0;
		for(int j=0;j<evList.size();j++){
			for(Integer oldIndex : evMap.get(evList.get(j))){				
				for(int k=0;k<temp.length;k++){
					temp[k][newIndex] = eigenVectorMatrix.get(k, oldIndex)/normFactor[oldIndex];
				}				
				evSorted[newIndex] = evList.get(j).doubleValue();
				newIndex++;
			}
		}		
		eigenVectorMatrix = new Matrix(temp);
		eigenValues = evSorted;			
	}
	
	public Instance transformData(Instance in){		
		return transformData(in, eigenValues.length);		
	}
	
	public Instance transformData(Instance in, boolean dimensionReduction){
		if(dimensionReduction){
			double sum = 0.0;
			double totalPer = 1.0;
			int discriminants = eigenValues.length;
			for(double ev : eigenValues){
				sum += ev;
			}
			for(int i=eigenValues.length-1;i>=0;i--){
				double per = eigenValues[i]/sum;
				if((totalPer-per)>DIMENSION_REDUCTION_PER){
					totalPer -= per;
					discriminants--;					
				}
			}
			System.out.println("LDA: dimension reduced to "+discriminants+" of "+eigenValues.length);
			return transformData(in, discriminants);
		}
		else{
			return transformData(in, eigenValues.length);
		}
	}
		
	public Instance transformData(Instance in, int discriminants){		
		double[][] eva = eigenVectorMatrix.getArray();
		double[][] tempEva = new double[eva.length][discriminants];		
		for(int i=0;i<eva.length;i++){
			tempEva[i] = Arrays.copyOf(eva[i], discriminants);
		}
		double[][] data = new double[1][in.numAttributes()-1];
		int di = 0;
		for(int i=0;i<in.numAttributes();i++){
			if(i!=in.classIndex()){
				data[0][di] = in.value(i);
				di++;
			}
		}
		Matrix evm = new Matrix(tempEva);
		Matrix dataM = new Matrix(data);
		Matrix resultM = (evm.transpose()).times(dataM.transpose());
		Instance result = new DenseInstance(in);
		double[][] r =  resultM.transpose().getArrayCopy();
		int ri = 0;
		for(int i=0;i<in.numAttributes();i++){
			if(i!=in.classIndex()){
				result.setValue(i, r[0][ri]);
				ri++;
			}
		}
		return result;
	}
	
	
	private static double getGroupMean(int column, ArrayList<ArrayList<Double>> data) {
		double[] d = new double[data.size()];
		for (int i = 0; i < data.size(); i++) {
			d[i] = data.get(i).get(column);
		}

		return getMean(d);
	}
	
	private static double getTotalMean(int column, Instances inst) {
		double[] d = new double[inst.numInstances()];
		for (int i = 0; i < inst.numInstances(); i++) {
			d[i] = inst.get(i).value(column);
		}

		return getMean(d);
	}
	
	/**
	 * Returns the mean of the given values. On error or empty data returns 0.
	 * 
	 * @param values The values.
	 * @return The mean.
	 */
	private static double getMean(final double[] values) {
		if (values == null || values.length == 0)
			return Double.NaN;

		double mean = 0.0d;

		for (int index = 0; index < values.length; index++){
			mean += values[index];
		}

		return mean / (double) values.length;
	}
	
	private static double[][] getCovarianceMatrix(ArrayList<ArrayList<Double>> matrix, double[] means){		
		int dimension = matrix.get(0).size();
		double[][] covariance = new double[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = i; j < dimension; j++) {
				double s = 0.0;
				for (int k = 0; k < matrix.size(); k++) {					
					s += (matrix.get(k).get(j) - means[j]) * (matrix.get(k).get(i) - means[i]);
				}
				s /= matrix.size();
				covariance[i][j] = s;
				covariance[j][i] = s;
			}
		}		
		return covariance;
	}

}
