package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Properties
import scala.collection.JavaConversions._

import akka.actor.Actor
import akka.event.EventHandler

import de.lmu.ifi.dbs.knowing.core.events._

import weka.core.{ Attribute, Instance, Instances }



/**
 * <p>An IProcessor encapsulates a data processing algorithm.
 * The main purpose is to ensure a highly parallel and robust
 * execution.</p>
 *
 * <p>The main concept behind this interface is question-&-answer.
 * Queries a executed and answered asynchronous.
 *
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 2011-04-15
 */
trait TProcessor extends Actor with TSender with TConfigurable {

  def receive = {
    case Register(actor) => addListener(actor)
    case Configure(p) =>
      configure(p)
      self reply Ready
    case Start => EventHandler.debug(this, "Running " + self.getActorClassName)
    case Query(q) => self reply Results(query(q))
    case Queries(q) =>
      val enum = q.enumerateInstances
      while (enum.hasMoreElements)
        self reply Results(query(enum.nextElement.asInstanceOf[Instance]))
    case Results(instances) => build(instances)
    case msg => EventHandler.warning(this,"<----> " + msg)
  }

  /**
   * <p>This method build the internal model which is used<br>
   * to answer queries.</p>
   *
   * <p>The build process should be implemented in an own<br>
   * thread, so other processors could build up their models<br>
   * too.</p>
   *
   * <p>Calling this method more than once should generate a<br>
   * new model based on the old one, instead of building a<br>
   * new model. For reseting the model use {@link #resetModel()}</p>
   *
   * @param the dataset
   */
  def build(instances: Instances)

  /**
   * <p>A query is answered via the interal model build by the buildModel method.<br>
   * The question should be proposed asynchronous and the answer will be sent<br>
   * asynchronous.</p>
   *
   * <p>Every query should run in it's own thread.</p>
   *
   * @param query - Instance with query
   * @return Instances - Query result
   */
  def query(query: Instance): Instances

  /**
   * <p>The presenter connected to this {@link IResultProcessor} calls this<br>
   * method to generate his initial presentation model. After that the<br>
   * presenter starts querying the processor.</p>
   *
   * @return - class labels
   */
  def getClassLabels: Array[String]

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
  def guessAndSetClassLabel(dataset: Instances): Int = {
    val index = dataset.classIndex
    index match {
      case -1 =>
        val cIndex = guessClassLabel(dataset)
        dataset.setClassIndex(cIndex)
        cIndex
      case x => x
    }
  }

  private def guessClassLabel(dataset: Instances): Int = {
    val classAttribute = dataset.attribute("class")
    if (classAttribute != null)
      return classAttribute.index

    //Maybe this is not the feastes way to do
    val attributes = dataset.enumerateAttributes().toList
    val nominal = attributes filter (a => a.asInstanceOf[Attribute].isNominal)
    nominal.headOption match {
      case Some(x) => x.asInstanceOf[Int]
      case None => -1

    }
  }

  def classLables(attribute: Attribute): Array[String] = {
    val enum = attribute.enumerateValues()
    var labels:List[String] = Nil
    while (enum.hasMoreElements) {
      val label = enum.nextElement().asInstanceOf[String]
      labels = label :: labels
    }
    labels.reverse.toArray
  }
}