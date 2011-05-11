package de.lmu.ifi.dbs.knowing.core.util

import java.util.{ ArrayList, Arrays, Collections, List, Properties }

import weka.core.{ Attribute, DenseInstance, Instances, ProtectedProperties }

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 24.04.2011
 *
 */
object ResultsUtil {
  val ATTRIBUTE_CLASS = "class";
  val ATTRIBUTE_PROBABILITY = "probability";
  val ATTRIBUTE_TIMESTAMP = "timestamp";
  val ATTRIBUTE_VALUE = "y";

  val NAME_CLASS_ONLY = "class_only";
  val NAME_CLASS_AND_PROBABILITY = "class_and_probability";
  val NAME_DATE_AND_VALUE = "date_and_value";
  val NAME_DATE_AND_VALUES = "date_and_values";

  val META_ATTRIBUTE_NAME = "name";

  /* ========================= */
  /* ==== Result Creation ==== */
  /* ========================= */

  /**
   * [p]
   * [li]relation name: {@link #NAME_CLASS_ONLY}[/li]
   * [li]attributes: {@link #ATTRIBUTE_CLASS}[/li]
   * [/p]
   * @param labels
   * @return {@link Instances}
   */
  def classOnlyResult(labels: List[String]): Instances = {
    val attributes = new ArrayList[Attribute]
    val classAttribute = new Attribute(ATTRIBUTE_CLASS, labels)
    attributes.add(classAttribute)
    val returns = new Instances(NAME_CLASS_ONLY, attributes, 0)
    returns.setClass(classAttribute)
    return returns
  }

  /**
   * [p]
   * [li]relation name: {@link #NAME_CLASS_AND_PROBABILITY}[/li]
   * [li]attributes: {@link #ATTRIBUTE_CLASS}, {@link #ATTRIBUTE_PROBABILITY}[/li]
   * [/p]
   *
   * @param labels
   * @return {@link Instances} with {@link Attribute}s: "class" and "probability"
   */
  def classAndProbabilityResult(labels: List[String]): Instances = {
    val attributes = new ArrayList[Attribute]

    val classAttribute = new Attribute(ATTRIBUTE_CLASS, labels)
    val probaAttribute = new Attribute(ATTRIBUTE_PROBABILITY)
    attributes.add(classAttribute)
    attributes.add(probaAttribute)

    val returns = new Instances(NAME_CLASS_ONLY, attributes, 0)
    returns.setClass(classAttribute)
    return returns
  }

  /**
   * [p]Adds [code]distribution.length[/code] instances to the dataset.[p]
   * [p]The lables list must have the same ordering as the distribution array[/p]
   *
   * @param labels
   * @param distribution
   * @return
   */
  def classAndProbabilityResult(labels: List[String], distribution: Array[Double]): Instances = {
    val returns = classAndProbabilityResult(labels)
    if (distribution.length != returns.numClasses())
      return returns

    val classAttribute = returns.attribute(ATTRIBUTE_CLASS)
    val probaAttribute = returns.attribute(ATTRIBUTE_PROBABILITY)
    for (i <- 0 until distribution.length) {
      val instance = new DenseInstance(2)
      instance.setValue(classAttribute, classAttribute.value(i))
      instance.setValue(probaAttribute, distribution(i))
      returns.add(instance)
    }
    return returns;
  }
  /**
   * [p]
   * [li]relation name: {@link #NAME_DATE_AND_VALUE}
   * [li]attributes: {@link #ATTRIBUTE_TIMESTAMP}, {@link #ATTRIBUTE_VALUE}
   * [/p]
   * @return
   */
  def dateAndValueResult: Instances = {
    val attributes = new ArrayList[Attribute]

    val timestampAttribute = new Attribute(ATTRIBUTE_TIMESTAMP, "yyyy-MM-dd'T'HH:mm:ss")
    val valueAttribute = new Attribute(ATTRIBUTE_VALUE + 0)
    attributes.add(timestampAttribute)
    attributes.add(valueAttribute)

    return new Instances(NAME_DATE_AND_VALUE, attributes, 0)
  }

  /**
   * [p]
   * Creates an Instances object with a DATE column and [code]names.size()[/code]
   * nummeric attributes. [br] All numeric attributes provide meta data with one
   * property {@link #META_ATTRIBUTE_NAME}.
   * [li]relation name: {@link #NAME_DATE_AND_VALUES}
   * [li]attributes: {@link #ATTRIBUTE_TIMESTAMP}, {@link #ATTRIBUTE_VALUE}+index
   * [/p]
   * @param names - the numeric attributes names -] accessable via meta data
   * @return
   */
  def dateAndValuesResult(names: List[String]): Instances = {
    val attributes = new ArrayList[Attribute]()

    attributes.add(new Attribute(ATTRIBUTE_TIMESTAMP, "yyyy-MM-dd'T'HH:mm:ss"))
    for (i <- 0 until names.size) {
      val props = new Properties()
      props.setProperty(META_ATTRIBUTE_NAME, names.get(i))
      val attribute = new Attribute(ATTRIBUTE_VALUE + i, new ProtectedProperties(props))
      attributes.add(attribute)
    }

    return new Instances(NAME_DATE_AND_VALUES, attributes, 0)
  }

  /* ========================= */
  /* === Result validation === */
  /* ========================= */

  /**
   * @param dataset
   * @return true - if structure equals to {@link #classOnlyResult(List)}
   */
  def isClassOnlyResult(dataset: Instances): Boolean = {
    if (dataset.numAttributes() != 1)
      return false
    return dataset.attribute(ATTRIBUTE_CLASS) != null
  }

  /**
   * @param dataset
   * @return true - if structure equals to {@link #classAndProbabilityResult(List)}
   */
  def isClassAndProbabilityResult(dataset: Instances): Boolean = {
    if (dataset.numAttributes() != 2)
      return false
    val classAttribute = dataset.attribute(ATTRIBUTE_CLASS)
    val probaAttribute = dataset.attribute(ATTRIBUTE_PROBABILITY)
    return classAttribute != null && probaAttribute != null
  }

  /* ========================= */
  /* ========= Utils ========= */
  /* ========================= */

  /**
   *  [p]Checks the dataset for class attribute in this order
   *  [li] {@link Instances#classIndex()} -] if ]= 0 returns index[/li]
   *  [li] returns index of the attribute named "class" if exists[/li]
   *  [li] returns index of the first nominal attribute[/li]
   *  [/p]
   *
   * @param dataset
   * @return class attribute index or -1
   */
  def guessClassIndex(dataset: Instances): Int = {
    val classIndex = dataset.classIndex();
    if (classIndex >= 0)
      return classIndex;

    val classAttribute = dataset.attribute("class");
    if (classAttribute != null)
      return classAttribute.index();

    //If no attribute named class was found, take the first nominal
    val attributes = dataset.enumerateAttributes()
    while (attributes.hasMoreElements) {
      val attribute = attributes.nextElement.asInstanceOf[Attribute]
      if (attribute.isNominal())
        return attribute.index()
    }
    return -1;
  }

  /**
   *
   * @param dataset
   * @return list with all numeric attributes created with {@link #ATTRIBUTE_VALUE} naming scheme
   */
  def findValueAttributes(dataset: Instances): List[Attribute] = {
    val returns = new ArrayList[Attribute]
    var i = 0
    var attribute = dataset.attribute(ATTRIBUTE_VALUE + i)
    while (attribute != null) {
      returns.add(attribute)
      i += 1
      attribute = dataset.attribute(ATTRIBUTE_VALUE + i)
    }
    return returns

  }

  /**
   *
   * @param dataset
   * @return list with all numeric attributes
   */
  def findNumericAttributes(dataset: Instances): List[Attribute] = {
    val returns = new ArrayList[Attribute]()
    val attributes = dataset.enumerateAttributes()
    while (attributes.hasMoreElements) {
      val attribute = attributes.nextElement.asInstanceOf[Attribute]
      if (attribute.isNumeric())
        returns add (attribute)
    }
    return returns
  }

}