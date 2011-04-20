package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.Actor
import weka.core.Instances
import weka.core.Instance

import de.lmu.ifi.dbs.knowing.core.events._

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
trait TProcessor extends Actor {

  def receive = {
    case Results(instances) => build(instances)
    case Query(q) => query(q)
    case Start(p) => log.info("Running with properties: " + p)
    case msg => log.info("Unkown message: " + msg)
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
  def query(query: Instance)

  /**
   * <p>The presenter connected to this {@link IResultProcessor} calls this<br>
   * method to generate his initial presentation model. After that the<br>
   * presenter starts querying the processor.</p>
   *
   * @return - class labels
   */
  def getClassLabels: Array[String]
}