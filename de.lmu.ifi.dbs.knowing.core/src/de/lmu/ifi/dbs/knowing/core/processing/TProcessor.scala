package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Properties
import scala.collection.JavaConversions._
import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer
import akka.actor.Actor
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.config.Supervision.Permanent
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import weka.core.{ Attribute, Instance, Instances }

import TSender.DEFAULT_PORT

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
  protected var isBuild = false
  protected val properties: Properties = new Properties

  //Stored Queries
  protected val queryQueue = Queue[(Option[ActorRef], Query)]()
  protected val queriesQueue = Queue[(Option[ActorRef], Queries)]()

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
    case Register(actor, in, out) => register(actor, in, out)
    case Configure(p) =>
      configure(p)
      properties.clear
      properties.putAll(p)
      statusChanged(Waiting())
    case Start | Start() => start

    //Process results
    case Results(inst, port) =>
      statusChanged(Running())
      isBuild = true
      build(inst, port)
      processStoredQueries
      self.sender match {
        case Some(s) => s ! Finished()
        case None => //Nothing
      }
      statusChanged(Ready())

    //Process single query
    case Query(q) => isBuild match {
      case true =>
        statusChanged(Running())
        processStoredQueries
        self reply QueryResults(query(q), q)
        statusChanged(Ready())
      case false => queryQueue += ((self.sender, Query(q)))
    }

    //Process multiple query
    case Queries(q, id) => isBuild match {
      case true =>
        statusChanged(Running())
        processStoredQueries
        self reply QueriesResults(queries(q))
        statusChanged(Ready())
      case false => queriesQueue += ((self.sender, Queries(q, id)))
    }
    //Process query result
    case QueryResults(r, q) =>
      statusChanged(Running())
      result(r, q)
      statusChanged(Ready())
    case QueriesResults(r) => r foreach { case (query, results) => result(results, query) }
    case Alive | Alive() => statusChanged(status)
    case msg => messageException(msg)
  }

  override def preRestart(reason: Throwable) {
    // clean up before restart
  }

  override def postRestart(reason: Throwable) {
    // reinit stable state after restart
  }

  def start = debug(this, "Running " + self.getActorClassName)

  def build: PartialFunction[(Instances, Option[String]), Unit] = { case (instances, _) => build(instances) }

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
   *
   */
  def queries(queries: Instances): List[(Instance, Instances)] = {
    val enum = queries.enumerateInstances
    val results = new ListBuffer[(Instance, Instances)]
    var i = 0
    while (enum.hasMoreElements) {
      val instance = enum.nextElement.asInstanceOf[Instance]
      results += ((instance, query(instance)))
      statusChanged(Progress(queries.relationName, i, queries.numInstances))
      i += 1
    }
    statusChanged(Ready())
    results toList
  }

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
  def messageException(message: Any) = {} //warning(this, "Unkown Message " + message)

  /**
   *  <p>Checks the dataset for class attribute in this order
   *  <li> {@link Instances#classIndex()} -> if >= 0 returns index</li>
   *  <li> returns index of attribute named "class" if exists</li>
   *  <li> returns index of first nominal attribute</li>
   *  <li> returns index of last attribute </li>
   *  </p>
   *
   * @param dataset
   * @return class attribute index or -1
   */
  def guessAndSetClassLabel(dataset: Instances, default: Int = -1): Int = {
    val index = dataset.classIndex
    index match {
      case -1 =>
        val cIndex = guessClassLabel(dataset)
        (cIndex, default) match {
          case (-1, -1) => -1
          case (-1, d) => dataset.setClassIndex(d); d
          case (guess, -1) => dataset.setClassIndex(guess); guess
        }
      case x => x
    }
  }

  def guessClassLabel(dataset: Instances): Int = {
    //TODO TProcessor.guesClassLabel -> guess class labels in relational datasets
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

  protected def cacheQuery(q: Instance) = queryQueue += ((self.sender, Query(q)))

  protected def cacheQuery(q: Instances) = queriesQueue += ((self.sender, Queries(q)))

  protected def processStoredQueries {
    //Does not respect arrival time
    while (queryQueue.nonEmpty) {
      val elem = queryQueue.dequeue
      elem._1 match {
        case None => //nothing
        case Some(s) => s ! QueryResults(query(elem._2.query), elem._2.query)
      }
    }
    while (queriesQueue.nonEmpty) {
      val elem = queriesQueue.dequeue
      elem._1 match {
        case None => //nothing
        //TODO Id must be send 
        case Some(s) => s ! QueriesResults(queries(elem._2.queries))
      }
    }
  }

  /**
   * <p>Sends the status change to the actors supervisor</p>
   */
  def statusChanged(status: Status) {
    if (this.status.equals(status))
      return
    this.status = status
    self.supervisor match {
      case Some(s) => s ! status
      case None => warning(this, "No supervisor defined!")
    }
  }
}

object TProcessor {

  val ABSOLUTE_PATH = INodeProperties.ABSOLUTE_PATH
}
