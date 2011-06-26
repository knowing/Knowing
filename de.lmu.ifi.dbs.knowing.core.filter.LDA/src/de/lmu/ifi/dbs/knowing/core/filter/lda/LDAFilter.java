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
	
	private boolean dimReduction = false;
	private static double SINGULARITY_DETECTION_THRESHOLD = 1.0E-60;
	private static final long serialVersionUID = 1L;
	
	private int dimension;
	private int discriminants;
	private Matrix eigenVectorMatrix;
	private double[] eigenValues;
	
	@Override
	public String globalInfo() {		
		return "This batch filter perfoms a linear discriminant analysis (LDA) on all numeric attributes of the instances.\n\n" +
				"For furhter details on the LDA have a look at the Article 'Eigenfaces vs. Fisherfaces: recognition using class " +
				"specific linear projection' by Belhumeur et al. (1997).";
	}
	
	@Override
	protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
		Instances result = new Instances(inputFormat, 0);
		if(dimReduction){
			int toDelete = dimension - discriminants;
			for(int i=inputFormat.numAttributes()-1; toDelete>0 && i>=0; i--){
				if(i!=inputFormat.classIndex() && inputFormat.attribute(i).isNumeric()){
					result.deleteAttributeAt(i);
					toDelete--;
				}
			}
		}
		return result;
	}
	
	@Override
	public Capabilities getCapabilities() {
	     Capabilities result = super.getCapabilities();	     
	     result.enable(Capability.DATE_ATTRIBUTES); // date attribute is accepted, but will only be passed through
	     result.enable(Capability.NUMERIC_ATTRIBUTES); // only numeric attributes are proccessed
	     result.enable(Capability.STRING_CLASS);  // filter needs a string class to be set
	     
	     return result;
	}
		
	public void setDimensionReduction(boolean b) throws Exception{
		this.dimReduction = b;
	}
	
	@Override
	protected Instances process(Instances inst) throws Exception {
		
		this.determineProjectionMatrix(inst);
		
		return this.transformData(inst,dimReduction);		
	}
	
	/**
	 * perfoms a Eigenvalue decomposition to determine the optimal projection matrix for the given Instances 
	 * @param inst the instances to calculate the LDA for
	 * @throws Exception throws an Exception if one of the covariance matrixes gets singular 
	 */
	private void determineProjectionMatrix(Instances inst) throws Exception{
		
		dimension = 0;
		for(int i=0;i<inst.numAttributes();i++){
			if(i!=inst.classIndex() && inst.attribute(i).isNumeric()){
				dimension++;
			}
		}
		
		//This cast is save -> capabilities allow only String classes
		ArrayList<String> classList = Collections.list((Enumeration<String>)inst.classAttribute().enumerateValues());								
		
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
						if(k!=inst.classIndex() && inst.attribute(k).isNumeric()){
							al.add(in.value(k));
						}
					}
					si.add(al);
				}
			}
			subset.add(i, si);
		}
		
		// calculate group mean
		double[][] groupMean = new double[subset.size()][dimension];
		for (int i = 0; i < groupMean.length; i++) {
			for (int j = 0; j < groupMean[i].length; j++) {
				groupMean[i][j] = getGroupMean(j, subset.get(i));
			}
		}

		// calculate total mean
		double[] totalMean = new double[dimension];
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
	
	
	/**
	 * sort the Eigenvalues in descendig order and rearrange the Eigenvektor matrix accordingly 
	 */
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
	
	private Instances transformData(Instances inst) throws Exception{		
		return transformData(inst, eigenValues.length);		
	}
	
	private Instances transformData(Instances inst, boolean dimensionReduction) throws Exception{
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
			return transformData(inst, discriminants);
		}
		else{
			return transformData(inst, eigenValues.length);
		}
	}
		
	private Instances transformData(Instances inst, int discriminants) throws Exception{		
		double[][] eva = eigenVectorMatrix.getArray();
		double[][] tempEva = new double[eva.length][discriminants];		
		for(int i=0;i<eva.length;i++){
			tempEva[i] = Arrays.copyOf(eva[i], discriminants);
		}
		double[][] data = new double[inst.numInstances()][dimension];
		
		for(int i=0;i<inst.numInstances();i++){
			Instance in = inst.get(i);
			int dj = 0;
			for(int j=0;j<inst.numAttributes();j++){				
				if(j!=in.classIndex() && in.attribute(j).isNumeric()){
					data[i][dj] = in.value(j);
					dj++;
				}
			}
		}
		
		Matrix evm = new Matrix(tempEva);
		Matrix dataM = new Matrix(data);
		Matrix resultM = (evm.transpose()).times(dataM.transpose());
		
		Instances result = new Instances(determineOutputFormat(inst), 0);
		double[][] rm =  resultM.transpose().getArrayCopy();
		
		for(int i=0;i<inst.numInstances();i++){
			Instance in = inst.get(i);
			Instance r = new DenseInstance(result.numAttributes());			
			int rj = 0;
			for(int j=0;j<result.numAttributes();j++){
				if(j!=in.classIndex() && in.attribute(j).isNumeric()){
					r.setValue(j, rm[i][rj]);
					rj++;
				}
				else{				
					r.setValue(j, in.value(j));
				}
			}
			result.add(r);
		}
		return result;
	}
	
	/**
	 * calculates the mean value for the column at the given index
	 * @param column index of the column to calculate the mean for
	 * @param data feature vectors to calculate the mean for
	 * @return mean value
	 */
	private static double getGroupMean(int column, ArrayList<ArrayList<Double>> data) {
		double[] d = new double[data.size()];
		for (int i = 0; i < data.size(); i++) {
			d[i] = data.get(i).get(column);
		}

		return getMean(d);
	}
	
	/**
	 * calculates the mean value for the attribute at the given index over all instances
	 * @param attIndex index of the attribute to calculate the mean for
	 * @param inst instances to calculate the mean for
	 * @return mean value
	 */
	private static double getTotalMean(int attIndex, Instances inst) {
		double[] d = new double[inst.numInstances()];
		for (int i = 0; i < inst.numInstances(); i++) {
			d[i] = inst.get(i).value(attIndex);
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
	
	/**
	 * calculates the covariance matrix for the given feature vectors
	 * @param fvs the the feature vectors to calculate the covariances for
	 * @param means array with mean values for each feature in the feature vector
	 * @return covariance matrix
	 */
	private static double[][] getCovarianceMatrix(ArrayList<ArrayList<Double>> fvs, double[] means){		
		int dimension = fvs.get(0).size();
		double[][] covariance = new double[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = i; j < dimension; j++) {
				double s = 0.0;
				for (int k = 0; k < fvs.size(); k++) {					
					s += (fvs.get(k).get(j) - means[j]) * (fvs.get(k).get(i) - means[i]);
				}
				s /= fvs.size();
				covariance[i][j] = s;
				covariance[j][i] = s;
			}
		}		
		return covariance;
	}

	

}
