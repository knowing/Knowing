package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Properties
import scala.collection.JavaConversions._

import akka.actor.Actor
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.config.Supervision.Permanent

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

  //Current status of processor
  protected var status: Status = Created()

  //Default lifeCylce 
  self.lifeCycle = Permanent

  def receive: Receive = customReceive orElse defaultReceive

  /**
   * <p>Override for special behaviour</p>
   */
  protected def customReceive: Receive = defaultReceive

  /**
   * <p>Default behaviour</p>
   */
  private def defaultReceive: Receive = {
    case Register(actor, port) => addListener(actor, port)
    case Configure(p) =>
      configure(p)
      if (self.getSender.isDefined) self reply Ready
      statusChanged(Ready())
    case Start | Start() => debug(this, "Running " + self.getActorClassName)
    case Results(inst) =>
      statusChanged(Running())
      build(inst)
      statusChanged(Ready())
    case Query(q) =>
      statusChanged(Running())
      self reply QueryResults(query(q), q)
      statusChanged(Ready())
    case Queries(q) =>
      val enum = q.enumerateInstances
      while (enum.hasMoreElements) {
        val instance = enum.nextElement.asInstanceOf[Instance]
        self reply QueryResults(query(instance), instance)
      }
    case QueryResults(r, q) =>
      statusChanged(Running())
      result(r, q)
      statusChanged(Ready())
    case Alive | Alive() => statusChanged(status)
    case msg => messageException(msg)
  }

  override def preRestart(reason: Throwable) {
    // clean up before restart
  }

  override def postRestart(reason: Throwable) {
    // reinit stable state after restart
  }

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
   * <p>After the processor sending a query, this method
   * is called if it gets a response</p>
   *
   * @param result - the results
   * @param query - the query
   */
  def result(result: Instances, query: Instance)

  /**
   * <p>Just puts a warning on the console and prints out the message</p>
   *
   */
  def messageException(message: Any) = warning(this, "Unkown Message " + message)

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

  /**
   * <p>Sends the status change to the actors supervisor</p>
   */
  protected def statusChanged(status: Status) {
    this.status = status
    self.supervisor match {
      case Some(s) => s ! status
      case None => warning(this, "No supervisor defined!")
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
      case Some(x) => x.asInstanceOf[Attribute].index
      case None => -1

    }
  }

  def classLables(attribute: Attribute): Array[String] = {
    val enum = attribute.enumerateValues()
    var labels: List[String] = Nil
    while (enum.hasMoreElements) {
      val label = enum.nextElement().asInstanceOf[String]
      labels = label :: labels
    }
    labels.reverse.toArray
  }
}