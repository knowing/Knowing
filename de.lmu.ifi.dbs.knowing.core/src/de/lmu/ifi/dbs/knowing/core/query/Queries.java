package de.lmu.ifi.dbs.knowing.core.query;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Queries for general purpose.
 * 
 * @author Nepomuk Seiler
 *
 */
public class Queries {

	/* ======================== */
	/* ==== Query Creation ==== */
	/* ======================== */
	
	public static Instances emptyQuery() {
		return new Instances("Empty", new ArrayList<Attribute>(0), 0);
	}

	/**
	 * <p>A fixed number of attributes called 'x0', 'x1', and so on.<br>
	 * The dataset contains only one {@link Instance} holding the<br>
	 * variable values.</p>
	 * 
	 * @param numVariables
	 * @return
	 */
	public static Instances vectorQuery(int numVariables) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(numVariables);
		for (int i = 0; i < numVariables; i++) {
			attributes.add(new Attribute("x" + i));
		}
		Instances dataset = new Instances("vector", attributes, 1);
		dataset.add(new DenseInstance(numVariables));
		return dataset;
	}
	
	/**
	 * <p>This dataset is intended to contain only one {@link Instance}
	 * for holding one value. The attribute name is 'value'</p> 
	 * 
	 * <p>However it's no problem to add more values. You may<br>
	 * change the attributes name from 'value' to 'index' so it<br>
	 * equivalent with {@link Queries#arrayNumericQuery()}<p>
	 *
	 * @return dataset with a numeric attribute value
	 */
	public static Instances singleNumericQuery() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(1);
		Instances dataset = new Instances("value", attributes, 1);
		dataset.add(new DenseInstance(1));
		return dataset;
	}
	
	/**
	 * <p>This dataset represent an one-dimensional array. The<br>
	 * attribute name is 'index'. The difference to {@link Queries#vectorQuery()} <br>
	 * is that is has only one attribute and each {@link Instance} represents <br>
	 * a value in the array.
	 * </p>
	 * 
	 * @return dataset with a numeric attribute index
	 */
	public static Instances arrayNumericQuery() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(1);
		attributes.add(new Attribute("index"));
		Instances dataset = new Instances("array", attributes, 1);
		return dataset;
	}
	
	public static Instances arrayNumericQuery(double[] array) {
		Instances dataset = arrayNumericQuery();
		for (double d : array) {
			dataset.add(new DenseInstance(1, new double[] {d}));
			//dataset.lastInstance().attribute(0).setWeight(d);
		}
		return dataset;
	}
	
	/* ======================== */
	/* == Query Translation === */
	/* ======================== */
	
	public static double[] arrayNumericQuery(Instances numericQuery) {
		Attribute attr = numericQuery.attribute("index");
		if(attr == null)
			return new double[0];
		if(!attr.isNumeric())
			return new double[0];
		return numericQuery.attributeToDoubleArray(attr.index());
	}
}
