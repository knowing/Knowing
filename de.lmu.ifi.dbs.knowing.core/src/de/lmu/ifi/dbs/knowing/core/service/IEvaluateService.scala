package de.lmu.ifi.dbs.knowing.core.service

import java.net.URI
import akka.actor.ActorRef
import java.io.{ InputStream, OutputStream }
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import scala.collection.mutable.{ Map => MutableMap }

/**
 * OSGi service responsible for starting a data mining process.
 *
 * @author Nepomuk Seiler
 * @version 0.2
 */
trait IEvaluateService {

  /**
   * Chooses the first UIFactory bind and fitting to the _DataProcessingUnit_.
   *
   * @param dpu - the DataProcessingUnit
   * @param execPath - executionPath to resolve relative properties
   */
  @throws(classOf[Exception])
  def evaluate(dpu: IDataProcessingUnit, execPath: URI): ActorRef

  /**
   * Executes the given dpu.
   * Choose UIFactory by ID
   *
   * @param dpu - the DataProcessingUnit
   * @param uiFactoryId - Id of the registered UIFactory
   * @param execPath - executionPath to resolve relative properties
   */
  @throws(classOf[Exception])
  def evaluate(dpu: IDataProcessingUnit, execPath: URI, uiFactoryId: String): ActorRef

  /**
   * Executes the given dpu.
   * Choose UIFactory by ID
   *
   * @param dpu - the DataProcessingUnit
   * @param uiFactory - choose uiSystem and where to present
   * @param execPath - executionPath to resolve relative properties
   */
  @throws(classOf[Exception])
  def evaluate(dpu: IDataProcessingUnit, execPath: URI, uiFactory: UIFactory[_]): ActorRef


  /**
   * Executes the given dpu.
   * Choose UIFactory by ID
   *
   * @param dpu - the DataProcessingUnit
   * @param uiFactory - choose uiSystem and where to present
   * @param execPath - executionPath to resolve relative properties
   */
  @throws(classOf[Exception])
  def evaluate(dpu: IDataProcessingUnit, execPath: URI,
    uiFactoryId: String,
    input: MutableMap[String, InputStream],
    output: MutableMap[String, OutputStream]): ActorRef

  /**
   * Executes the given dpu.
   * Provide UIFactory
   *
   * @param dpu - the DataProcessingUnit
   * @param uiFactory - choose uiSystem and where to present
   * @param execPath - executionPath to resolve relative properties
   */
  @throws(classOf[Exception])
  def evaluate(dpu: IDataProcessingUnit, execPath: URI,
    ui: UIFactory[_],
    input: MutableMap[String, InputStream],
    output: MutableMap[String, OutputStream]): ActorRef

}