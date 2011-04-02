/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.ProtectedProperties;

/**
 * <p>General purpose datasets</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 31.03.2011
 *
 */
public class Results {
	
	public static final String ATTRIBUTE_CLASS = "class";
	public static final String ATTRIBUTE_PROBABILITY = "probability";
	public static final String ATTRIBUTE_TIMESTAMP = "timestamp";
	public static final String ATTRIBUTE_VALUE = "y";
	
	public static final String NAME_CLASS_ONLY = "class_only";
	public static final String NAME_CLASS_AND_PROBABILITY = "class_and_probability";
	public static final String NAME_DATE_AND_VALUE = "data_and_value";
	public static final String NAME_DATE_AND_VALUES = "data_and_values";
	
	public static final String META_ATTRIBUTE_NAME = "name";

	/* ========================= */
	/* ==== Result Creation ==== */
	/* ========================= */
	
	/**
	 * <p>
	 * <li>relation name: {@link #NAME_CLASS_ONLY}</li>
	 * <li>attributes: {@link #ATTRIBUTE_CLASS}</li>
	 * </p>
	 * @param labels
	 * @return {@link Instances} 
	 */
	public static Instances classOnlyResult(List<String> labels) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		Attribute classAttribute = new Attribute(ATTRIBUTE_CLASS, labels);
		attributes.add(classAttribute);
		Instances returns = new Instances(NAME_CLASS_ONLY, attributes, 0);
		returns.setClass(classAttribute);
		return returns;
	}
	
	/**
	 * <p>
	 * <li>relation name: {@link #NAME_CLASS_AND_PROBABILITY}</li>
	 * <li>attributes: {@link #ATTRIBUTE_CLASS}, {@link #ATTRIBUTE_PROBABILITY}</li>
	 * </p>
	 * 
	 * @param labels
	 * @return {@link Instances} with {@link Attribute}s: "class" and "probability"
	 */
	public static Instances classAndProbabilityResult(List<String> labels) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		
		Attribute classAttribute = new Attribute(ATTRIBUTE_CLASS, labels);
		Attribute probaAttribute = new Attribute(ATTRIBUTE_PROBABILITY);
		attributes.add(classAttribute);
		attributes.add(probaAttribute);
		
		Instances returns = new Instances(NAME_CLASS_ONLY, attributes, 0);
		returns.setClass(classAttribute);
		return returns;
	}
	
	/**
	 * <p>Adds <code>distribution.length</code> instances to the dataset.<p>
	 * <p>The lables list must have the same ordering as the distribution array</p>
	 * 
	 * @param labels
	 * @param distribution
	 * @return
	 */
	public static Instances classAndProbabilityResult(List<String> labels, double[] distribution) {		
		Instances returns = classAndProbabilityResult(labels);
		if(distribution.length != returns.numClasses())
			return returns;
		
		
		Attribute classAttribute = returns.attribute(ATTRIBUTE_CLASS);
		Attribute probaAttribute = returns.attribute(ATTRIBUTE_PROBABILITY);
		for (int i = 0; i < distribution.length; i++) {
			DenseInstance instance = new DenseInstance(2);
			instance.setValue(classAttribute, classAttribute.value(i));
			instance.setValue(probaAttribute, distribution[i]);
			returns.add(instance);
		}
		return returns;
	}
	/**
	 * <p>
	 * <li>relation name: {@link #NAME_DATE_AND_VALUE}
	 * <li>attributes: {@link #ATTRIBUTE_TIMESTAMP}, {@link #ATTRIBUTE_VALUE}
	 * </p>
	 * @return
	 */
	public static Instances dateAndValueResult() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		
		Attribute timestampAttribute = new Attribute(ATTRIBUTE_TIMESTAMP, "yyyy-MM-dd'T'HH:mm:ss");
		Attribute valueAttribute = new Attribute(ATTRIBUTE_VALUE);
		attributes.add(timestampAttribute);
		attributes.add(valueAttribute);
		
		Instances returns = new Instances(NAME_DATE_AND_VALUE, attributes, 0);
		return returns;
	}
	
	/**
	 * <p>
	 * Creates an Instances object with a DATE column and <code>names.size()</code> 
	 * nummeric attributes. <br> All numeric attributes provide meta data with one
	 * property {@link #META_ATTRIBUTE_NAME}.
	 * <li>relation name: {@link #NAME_DATE_AND_VALUES}
	 * <li>attributes: {@link #ATTRIBUTE_TIMESTAMP}, {@link #ATTRIBUTE_VALUE}+index
	 * </p>
	 * @param names - the numeric attributes names -> accessable via meta data
	 * @return
	 */
	public static Instances dateAndValuesResult(List<String> names) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		
		Attribute timestampAttribute = new Attribute(ATTRIBUTE_TIMESTAMP, "yyyy-MM-dd'T'HH:mm:ss");
		attributes.add(timestampAttribute);
		
		int i = 1;
		for (String name : names) {
			Properties props = new Properties();
			props.setProperty(META_ATTRIBUTE_NAME, name);
			Attribute attribute = new Attribute(ATTRIBUTE_VALUE + i++, new ProtectedProperties(props));
			attributes.add(attribute);
		}
		
		Instances returns = new Instances(NAME_DATE_AND_VALUES, attributes, 0);
		return returns;
	}
	
	/**
	 * 
	 * @param names
	 * @return
	 * @see #dateAndValuesResult(List)
	 */
	public static Instances dateAndValuesResult(String[] names) {
		return dateAndValuesResult(Arrays.asList(names));
	}
	
	/* ========================= */
	/* === Result validation === */
	/* ========================= */
	
	/**
	 * @param dataset
	 * @return true - if structure equals to {@link #classOnlyResult(List)}
	 */
	public static boolean isClassOnlyResult(Instances dataset) {
		if(dataset.numAttributes() != 1)
			return false;
		return dataset.attribute(ATTRIBUTE_CLASS) != null;
	}
	
	/**
	 * @param dataset
	 * @return true - if structure equals to {@link #classAndProbabilityResult(List)}
	 */
	public static boolean isClassAndProbabilityResult(Instances dataset) {
		if(dataset.numAttributes() != 2)
			return false;
		Attribute classAttribute = dataset.attribute(ATTRIBUTE_CLASS);
		Attribute probaAttribute = dataset.attribute(ATTRIBUTE_PROBABILITY);
		return  classAttribute != null && probaAttribute != null;
	}
	
	
	/* ========================= */
	/* ========= Utils ========= */
	/* ========================= */
	
	/**
	 *  <p>Checks the dataset for class attribute in this order
	 *  <li> {@link Instances#classIndex()} -> if >= 0 returns index</li>
	 *  <li> returns index of the attribute named "class" if exists</li>
	 *  <li> returns index of the first nominal attribute</li>
	 *  </p>
	 *  
	 * @param dataset
	 * @return class attribute index or -1
	 */
	public static int guessClassIndex(Instances dataset) {
		int classIndex = dataset.classIndex();
		if(classIndex >= 0)
			return classIndex;
		
		Attribute classAttribute = dataset.attribute("class");
		if(classAttribute != null)
			return classAttribute.index();
		
		//If no attribute named class was found, take the first nominal
		ArrayList<Attribute> attributes = Collections.list(dataset.enumerateAttributes());
		for (Attribute attribute : attributes) {
			if (attribute.isNominal())
				return attribute.index();
		}
		return -1;
	}
	
	/**
	 * 
	 * @param dataset
	 * @return list with all numeric attributes created with {@link #ATTRIBUTE_VALUE} naming scheme
	 */
	public static List<Attribute> findValueAttributes(Instances dataset) {
		List<Attribute> returns = new ArrayList<Attribute>();
		int i = 1;
		Attribute attribute = dataset.attribute(ATTRIBUTE_VALUE + i++);
		while(attribute != null) {
			returns.add(attribute);
			attribute = dataset.attribute(ATTRIBUTE_VALUE + i++);
		}
		return returns;
		
	}
	
	/**
	 * 
	 * @param dataset
	 * @return list with all numeric attributes
	 */
	public static List<Attribute> findNumericAttributes(Instances dataset) {
		List<Attribute> returns = new ArrayList<Attribute>();
		ArrayList<Attribute> attributes = Collections.<Attribute>list(dataset.enumerateAttributes());
		for (Attribute attribute : attributes) {
			if(attribute.isNumeric())
				returns.add(attribute);
		}
		return returns;
	}
	
}
