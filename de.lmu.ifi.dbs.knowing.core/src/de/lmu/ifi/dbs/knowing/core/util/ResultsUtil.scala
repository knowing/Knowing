package de.lmu.ifi.dbs.knowing.core.util

import java.util.{ ArrayList, Arrays, Collections, List => JList, Properties, Map => JMap }
import scala.collection.JavaConversions._
import weka.core.{ Attribute, DenseInstance, Instances, Instance, ProtectedProperties, WekaException }
import weka.core.SparseInstance

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 24.04.2011
 *
 */
object ResultsUtil {
  val ATTRIBUTE_CLASS = "class"
  val ATTRIBUTE_CLASS_DISTRIBUTION = "class_distribution"
  val ATTRIBUTE_PROBABILITY = "probability"
  val ATTRIBUTE_TIMESTAMP = "timestamp"
  val ATTRIBUTE_VALUE = "y"
  val ATTRIBUTE_FROM = "from"
  val ATTRIBUTE_TO = "to"
  val ATTRIBUTE_SOURCE = "source"

  val NAME_EMPTY = "empty"
  val NAME_CLASS_ONLY = "class_only"
  val NAME_CLASS_AND_PROBABILITY = "class_and_probability"
  val NAME_CROSS_VALIDATION = "cross_validation"
  val NAME_CLASS_DISTRIBUTION = "class_distribution"
  val NAME_TIME_INTERVAL = "time_interval"
  val NAME_TIME_SERIES = "time_series"

  val META_ATTRIBUTE_NAME = "name"

  val DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss:SSS"

  val ORIGINAL_INSTANCES = "original"

  /* ========================= */
  /* ==== Result Creation ==== */
  /* ========================= */

  /**
   * Empty Instances
   */
  def emptyResult: Instances = new Instances(NAME_EMPTY, new ArrayList[Attribute], 0)

  /**
   * <p>
   * <li>relation name: {@link #NAME_CLASS_ONLY}</li>
   * <li>attributes: {@link #ATTRIBUTE_CLASS}</li>
   * </p>
   * @param labels
   * @return {@link Instances}
   */
  def classOnlyResult(labels: List[String]): Instances = {
    val attributes = new ArrayList[Attribute]
    val classAttribute = new Attribute(ATTRIBUTE_CLASS, labels)
    attributes.add(classAttribute)
    val returns = new Instances(NAME_CLASS_ONLY, attributes, 0)
    returns.setClass(classAttribute)
    returns
  }

  /**
   * <p>
   * <li>relation name: {@link #NAME_CLASS_ONLY}</li>
   * <li>attributes: {@link #ATTRIBUTE_CLASS}</li>
   * </p>
   * @param labels
   * @return {@link Instances}
   */
  def classOnlyResult(labels: JList[String]): Instances = classOnlyResult(labels.toList)

  /**
   * <p>
   * <li>relation name: {@link #NAME_CLASS_AND_PROBABILITY}</li>
   * <li>attributes: {@link #ATTRIBUTE_CLASS}, {@link #ATTRIBUTE_PROBABILITY}</li>
   * </p>
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

    val returns = new Instances(NAME_CLASS_AND_PROBABILITY, attributes, 0)
    returns.setClass(classAttribute)
    returns
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
  def classAndProbabilityResult(labels: JList[String]): Instances = classAndProbabilityResult(labels.toList)

  /**
   * <p>Adds [code]distribution.length[/code] instances to the dataset.<p>
   * <p>The lables list must have the same ordering as the distribution array</p>
   *
   * @param labels
   * @param distribution
   * @return
   */
  def classAndProbabilityResult(labels: List[String], distribution: Array[Double]): Instances = {
    val returns = classAndProbabilityResult(labels)
    if (distribution.length > returns.numClasses)
      return returns

    val classAttribute = returns.attribute(ATTRIBUTE_CLASS)
    val probaAttribute = returns.attribute(ATTRIBUTE_PROBABILITY)
    for (i <- 0 until distribution.length) {
      val instance = new DenseInstance(2)
      instance.setValue(classAttribute, classAttribute.value(i))
      instance.setValue(probaAttribute, distribution(i))
      returns.add(i, instance)
    }
    //Just in case
    for (i <- distribution.length until (labels.size - distribution.length)) {
      val instance = new DenseInstance(2)
      instance.setValue(classAttribute, classAttribute.value(i))
      instance.setValue(probaAttribute, 0)
      returns.add(i, instance)
    }
    returns
  }

  /**
   * <p>Adds [code]distribution.length[/code] instances to the dataset.<p>
   * <p>The lables list must have the same ordering as the distribution array</p>
   *
   * @param labels
   * @param distribution
   * @return
   */
  def classAndProbabilityResult(labels: JList[String], distribution: Array[Double]): Instances = classAndProbabilityResult(labels.toList)

  /**
   * <p>
   * Creates an Instances object with a DATE column and [code]names.size()[/code]
   * nummeric attributes. [br] All numeric attributes provide meta data with one
   * property {@link #META_ATTRIBUTE_NAME}.
   * <li>relation name: {@link #NAME_DATE_AND_VALUES}
   * <li>attributes: {@link #ATTRIBUTE_TIMESTAMP}, {@link #ATTRIBUTE_VALUE}+index
   * </p>
   * @param names - the numeric attributes names -] accessable via meta data
   * @return
   */
  def timeSeriesResult(names: List[String], datePattern: String): Instances = {
    val attributes = new ArrayList[Attribute]

    attributes.add(new Attribute(ATTRIBUTE_TIMESTAMP, datePattern))
    for (i <- 0 until names.size) {
      val props = new Properties()
      props.setProperty(META_ATTRIBUTE_NAME, names.get(i))
      val attribute = new Attribute(ATTRIBUTE_VALUE + i, new ProtectedProperties(props))
      attributes.add(attribute)
    }
    new Instances(NAME_TIME_SERIES, attributes, 0)
  }

  /**
   * @see timeSeriesResult(names, datePattern)
   */
  def timeSeriesResult(names: List[String]): Instances = timeSeriesResult(names, DATETIME_PATTERN)

  /**
   * <p>
   * Creates an Instances object with a DATE column and [code]names.size()[/code]
   * nummeric attributes. [br] All numeric attributes provide meta data with one
   * property {@link #META_ATTRIBUTE_NAME}.
   * <li>relation name: {@link #NAME_DATE_AND_VALUES}
   * <li>attributes: {@link #ATTRIBUTE_TIMESTAMP}, {@link #ATTRIBUTE_VALUE}+index
   * </p>
   * @param names - the numeric attributes names -] accessable via meta data
   * @return
   */
  def timeSeriesResult(names: JList[String]): Instances = timeSeriesResult(names.toList)

  def timeSeriesResult(names: JList[String], datePattern: String): Instances = timeSeriesResult(names.toList, datePattern)

  /**
   * Columns: One column for every label
   * Rows: One row for every label
   *
   * index of row/column -> names.index
   *
   * @param names - class label names
   * @return
   */
  def confusionMatrix(classes: List[String]): Instances = {
    val attributes = new ArrayList[Attribute]
    classes foreach (name => attributes.add(new Attribute(name)))
    val dataset = new Instances(NAME_CROSS_VALIDATION, attributes, classes.length)
    for (i <- 0 until classes.length)
      dataset.add(i, new DenseInstance(1.0, Array.fill(classes.length) { 0.0 }))
    dataset
  }

  /**
   * <p>Columns: One column for every label</p>
   * <li>Rows: One row for every label</li>
   *
   * <p>index of row/column -> names.index</p>
   *
   * @param names - class label names
   * @return
   */
  def confusionMatrix(classes: JList[String]): Instances = confusionMatrix(classes.toList)

  /**
   * <p>Creates a Result-Instance for TimeInterval data</p>
   *
   * from | to | class
   *
   * @param lables - class labels
   * @return Instances
   */
  def timeIntervalResult(labels: List[String]): Instances = {
    val attributes = new ArrayList[Attribute]
    attributes.add(new Attribute(ATTRIBUTE_FROM, DATETIME_PATTERN))
    attributes.add(new Attribute(ATTRIBUTE_TO, DATETIME_PATTERN))
    attributes.add(new Attribute(ATTRIBUTE_CLASS, labels))
    val dataset = new Instances(NAME_TIME_INTERVAL, attributes, 0)
    dataset.setClass(dataset.attribute(ATTRIBUTE_CLASS))
    dataset
  }

  /**
   * <p>Creates a Result-Instance for TimeInterval data</p>
   *
   * from | to | class
   *
   * @param lables - class labels
   * @return Instances
   */
  def timeIntervalResult(labels: JList[String]): Instances = timeIntervalResult(labels.toList)

  /* ========================= */
  /* === Result validation === */
  /* ========================= */

  def isEmptyResult(dataset: Instances): Boolean = {
    dataset.relationName.equals(NAME_EMPTY) || (dataset.numAttributes == 0 && dataset.numInstances == 0)
  }

  /**
   * @param dataset
   * @return true - if structure equals to {@link #classOnlyResult(List)}
   */
  def isClassOnlyResult(dataset: Instances): Boolean = {
    if (dataset.numAttributes() != 1)
      return false
    dataset.attribute(ATTRIBUTE_CLASS) != null
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
    classAttribute != null && probaAttribute != null
  }

  /* ========================= */
  /* ========= Utils ========= */
  /* ========================= */

  /**
   *  <p>Checks the dataset for class attribute in this order
   *  <li> {@link Instances#classIndex()} -] if ]= 0 returns index</li>
   *  <li> returns index of the attribute named "class" if exists</li>
   *  <li> returns index of the first nominal attribute</li>
   *  </p>
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
    -1;
  }

  /**
   *
   * @param dataset
   * @return list with all numeric attributes created with {@link #ATTRIBUTE_VALUE} naming scheme
   */
  def findValueAttributes(dataset: Instances): JList[Attribute] = {
    val returns = new ArrayList[Attribute]
    var i = 0
    var attribute = dataset.attribute(ATTRIBUTE_VALUE + i)
    while (attribute != null) {
      returns.add(attribute)
      i += 1
      attribute = dataset.attribute(ATTRIBUTE_VALUE + i)
    }
    returns

  }

  /**
   *
   * @param dataset
   * @return map with META_ATTRIBUTE_NAME -> Attribute
   */
  def findValueAttributesAsMap(dataset: Instances): Map[String, Attribute] = {
    var returns: Map[String, Attribute] = Map()
    var i = 0
    var attribute = dataset.attribute(ATTRIBUTE_VALUE + i)
    while (attribute != null) {
      val name = attribute.getMetadata.getProperty(META_ATTRIBUTE_NAME)
      if (name == null || name.isEmpty)
        returns += (attribute.name -> attribute)
      else
        returns += (name -> attribute)
      i += 1
      attribute = dataset.attribute(ATTRIBUTE_VALUE + i)
    }
    returns
  }

  /**
   *
   * @param dataset
   * @return map with META_ATTRIBUTE_NAME -> Attribute
   */
  def findValueAttributesAsJavaMap(dataset: Instances): java.util.Map[String, Attribute] = findValueAttributesAsMap(dataset).toMap[String, Attribute]

  /**
   *
   * @param dataset
   * @return list with all numeric attributes
   */
  def findNumericAttributes(dataset: Instances): JList[Attribute] = {
    val returns = new ArrayList[Attribute]()
    val attributes = dataset.enumerateAttributes()
    while (attributes.hasMoreElements) {
      val attribute = attributes.nextElement.asInstanceOf[Attribute]
      if (attribute.isNumeric())
        returns add (attribute)
    }
    returns
  }

  /**
   * Just appends one 'append' to 'first' without changing attributes.
   *
   * @throws WekaException - if headers are not equal
   * @returns Instances - new instances object with first -> append added
   */
  @throws(classOf[WekaException])
  def appendInstances(first: Instances, append: Instances): Instances = {
    //    println("First: " + first)
    //    println("Append: " + append)
    if (!first.equalHeaders(append))
      throw new WekaException("Instances headers are not equal")
    val ret = new Instances(first, first.numInstances + append.numInstances)
    val firstEnum = first.enumerateInstances
    while (firstEnum.hasMoreElements) ret.add(firstEnum.nextElement.asInstanceOf[Instance])
    val appendEnum = append.enumerateInstances
    while (appendEnum.hasMoreElements) ret.add(appendEnum.nextElement.asInstanceOf[Instance])
    ret
  }
  
  //TODO ResultsUtil.appendInstances -> Doesn't respect relational Attributes
  
  /**
   * This method merges a list of instances into a given header. The header is normally empty.
   */
  def appendInstances(header: Instances, datasets: List[Instances]): Instances = appendInstances(header, datasets, identity)

  /**
   * This method merges a list of instance into a given header and manipulates every instance before adding.
   */
  def appendInstances(header: Instances, datasets: List[Instances], f: Instances => Instances): Instances = {
    datasets.foldLeft(header)((result, inst) => appendInstances(result, f(inst)))
  }

  /**
   * This method merges a list of instance into a given header and manipulates every instance before adding.
   * The first tupel value can be used to give specific merge details for each instance.
   */
  def appendInstancesTupel[A](header: Instances, datasets: List[(A, Instances)], f: (A, Instances) => Instances): Instances = {
    datasets.foldLeft(header)((result, inst) => appendInstances(result, f(inst._1, inst._2)))
  }

  /**
   * <p>Splits a instances object with the given attribute into a map of source -> Instances
   * If 'attribute' is not defined, the method returns ORIGINAL_INSTANCES -> instances</p>
   *
   * @param instances - Instances to split
   * @param attribute - name of the attribute, the split should be performed on
   * @returns source -> Instances
   *
   */
  def splitInstanceByAttribute(instances: Instances, attribute: String, removeAttr: Boolean = true): Map[String, Instances] = {
    val splitAttr = instances.attribute(attribute)
    if (splitAttr == null)
      return Map(ORIGINAL_INSTANCES -> instances)
    val instList = instances toList
    val classMap = instList.groupBy(inst => splitAttr.value(inst.value(splitAttr) toInt))
    classMap map {
      case (clazz, list) =>
        val ret = new Instances(instances, list.length)
        list foreach (ret.add(_))
        if (removeAttr) ret.deleteAttributeAt(splitAttr.index)
        //println("splitted: " + ret)
        (clazz, ret)
    }
  }

  /**
   * @see splitInstanceByAttribute
   */
  def splitInstanceByAttributeJava(instances: Instances, attribute: String, removeAttr: Boolean = true): JMap[String, Instances] = asMap(splitInstanceByAttribute(instances, attribute, removeAttr))

  /**
   * <p>Splits a instances object with SOURCE_ATTRIBUTE into a map of source -> Instances
   * If no SOURCE_ATTRIBUTE is defined, the method returns ORIGINAL_INSTANCES -> instances</p>
   *
   * @param instances - Instances to split
   * @returns source -> Instances
   *
   */
  def splitInstanceBySource(instances: Instances, removeAttr: Boolean = true): Map[String, Instances] = splitInstanceByAttribute(instances, ATTRIBUTE_SOURCE, removeAttr)

  /**
   * @see splitInstanceBySource
   */
  def splitInstanceBySourceJava(instances: Instances, removeAttr: Boolean = true): JMap[String, Instances] = splitInstanceByAttributeJava(instances, ATTRIBUTE_SOURCE, removeAttr)

  /**
   *
   */
  def highestProbability(distribution: Instance): (Double, String) = {
    val classAttr = distribution.enumerateAttributes.toList filter {
      case a: Attribute => a.name.size > 5 && a.name.startsWith("class")
    }

    classAttr.foldLeft((0.0, "")) {
      case ((max, clazz), a: Attribute) =>
        val value = distribution.value(a)
        if (value > max) (value, a.name.substring(5))
        else (max, clazz)
    }
  }

  /**
   * class_and_probability must contain ALL class labels.
   *
   * Appends classDistribution as follows: Instances-Attributes + class + classA + classB + classC..
   * TODO class is set to highestProbability
   *
   * @param header - is used for dataset creation
   * @param inst - contains query -> class_and_probability
   * @return new Instances object with appended classDistribution
   */
  def appendClassDistribution(header: Instances, inst: Map[Instance, Instances]): Instances = {
    val head = new Instances(header, inst.size)
    val labels = header.classAttribute.enumerateValues.toList
    labels foreach (l => head.insertAttributeAt(new Attribute("class" + l), head.numAttributes))
    inst.foldLeft(head) {
      case (head, (query, dist)) =>
        val inst = new DenseInstance(head.numAttributes)
        val numAttr = query.numAttributes
        //Copy values
        for (i <- 0 until numAttr) inst.setValue(i, query.value(i))
        //Fill in distribution
        for (i <- numAttr until (numAttr + labels.size)) inst.setValue(i, dist.get(i - numAttr).value(1))
        head.add(inst)
        head
    }
  }
}