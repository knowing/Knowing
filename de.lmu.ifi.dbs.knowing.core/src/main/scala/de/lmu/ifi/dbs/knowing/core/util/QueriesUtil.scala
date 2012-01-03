package de.lmu.ifi.dbs.knowing.core.util

import java.util.ArrayList

import weka.core.{ Attribute, DenseInstance , Instance, Instances }

/**
 * Queries for general purpose.
 * 
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 25.04.2011
 *
 */
object QueriesUtil {

  /* ======================== */
	/* ==== Query Creation ==== */
	/* ======================== */
	
	def emptyQuery:Instances = {
		return new Instances("Empty", new ArrayList[Attribute](0), 0);
	}

	/**
	 * [p]A fixed number of attributes called 'x0', 'x1', and so on.[br]
	 * The dataset contains only one {@link Instance} holding the[br]
	 * variable values.[/p]
	 * 
	 * @param numVariables
	 * @return
	 */
	def vectorQuery(numVariables: Int):Instances = {
		val attributes = new ArrayList[Attribute](numVariables)
		for(i <- 0 until numVariables) {
		  attributes add(new Attribute("x" + i))
		}
		val dataset = new Instances("vector", attributes, 1)
		dataset add(new DenseInstance(numVariables))
		return dataset
	}
	
	/**
	 * [p]This dataset is intended to contain only one {@link Instance}
	 * for holding one value. The attribute name is 'value'[/p] 
	 * 
	 * [p]However it's no problem to add more values. You may[br]
	 * change the attributes name from 'value' to 'index' so it[br]
	 * equivalent with {@link Queries#arrayNumericQuery()}[p]
	 *
	 * @return dataset with a numeric attribute value
	 */
	def singleNumericQuery:Instances = {
		val attributes = new ArrayList[Attribute](1)
		val dataset = new Instances("value", attributes, 1)
		dataset add(new DenseInstance(1))
		return dataset
	}
	
	/**
	 * [p]This dataset represent an one-dimensional array. The[br]
	 * attribute name is 'index'. The difference to {@link Queries#vectorQuery()} [br]
	 * is that is has only one attribute and each {@link Instance} represents [br]
	 * a value in the array.
	 * [/p]
	 * 
	 * @return dataset with a numeric attribute index
	 */
	def arrayNumericQuery:Instances = {
		val attributes = new ArrayList[Attribute](1)
		attributes add(new Attribute("index"))
		val dataset = new Instances("array", attributes, 1)
		return dataset
	}
	
	def arrayNumericQuery(array:Array[Double]):Instances = {
		val dataset = arrayNumericQuery
		array foreach (d => dataset add(new DenseInstance(1, Array {d})))
		return dataset;
	}
	
	/* ======================== */
	/* == Query Translation === */
	/* ======================== */
	
	def arrayNumericQuery(numericQuery:Instances ): Array[Double] = {
		val attr = numericQuery.attribute("index");
		if(attr == null)
			return Array()
		if(!attr.isNumeric())
			return Array()
		return numericQuery.attributeToDoubleArray(attr.index());
	}
}