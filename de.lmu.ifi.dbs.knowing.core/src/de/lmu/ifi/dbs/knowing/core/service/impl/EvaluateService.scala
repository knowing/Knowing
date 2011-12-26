package de.lmu.ifi.dbs.knowing.core.service.impl

import java.net.URI
import java.io.{ InputStream, OutputStream }
import akka.actor.Actor.actorOf
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.processing.DPUExecutor
import de.lmu.ifi.dbs.knowing.core.service.{ IEvaluateService, IFactoryDirectory }
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil
import scala.collection.mutable.{ Map => MutableMap, HashMap }
import scala.collection.mutable.ArrayBuffer

/**
 * Default implementation for the EvaluationService
 *
 * @author Nepomuk Seiler
 * @version 0.2
 */
class EvaluateService extends IEvaluateService {

  /** 1..1 relation */
  private var factoryDirectory: IFactoryDirectory = _

  /** 0..n relation */
  private var uiFactories = new ArrayBuffer[UIFactory[_]]()

  /**
   * Instantiates DPUExecutor and runs the DPU
   * @see IEvaluationService
   */
  @throws(classOf[Exception])
  def evaluate(dpu: IDataProcessingUnit, execPath: URI): ActorRef = {
    uiFactories.size match {
      case 0 => throw new Exception("No UIFactory registered")
      case 1 => evaluate(dpu, execPath, uiFactories(0), HashMap[String, InputStream](), HashMap[String, OutputStream]())
      case x =>
        //TODO search for best fitting UI factory. Use service properties and presenter properties
        evaluate(dpu, execPath, uiFactories(0), HashMap[String, InputStream](), HashMap[String, OutputStream]())
    }
  }

  /**
   * @param dpu - the DataProcessingUnit
   * @param uiFactoryId - Id of the registered UIFactory
   * @param execPath - executionPath to resolve relative properties
   */
  @throws(classOf[Exception])
  def evaluate(dpu: IDataProcessingUnit, execPath: URI, uiFactoryId: String): ActorRef = {
    evaluate(dpu, execPath, uiFactoryId, HashMap[String, InputStream](), HashMap[String, OutputStream]())
  }

  /**
   * @param dpu - the DataProcessingUnit
   * @param uiFactory - choose uiSystem and where to present
   * @param execPath - executionPath to resolve relative properties
   */
  @throws(classOf[Exception])
  def evaluate(dpu: IDataProcessingUnit, execPath: URI, uiFactory: UIFactory[_]): ActorRef = {
    evaluate(dpu, execPath, uiFactory, HashMap[String, InputStream](), HashMap[String, OutputStream]())
  }

  /**
   * Instantiates DPUExecturo and runs the DPU
   * @see IEvaluationService
   */
  @throws(classOf[Exception])
  def evaluate(dpu: IDataProcessingUnit, execPath: URI,
    uiFactoryId: String,
    input: MutableMap[String, InputStream],
    output: MutableMap[String, OutputStream]): ActorRef = {
    uiFactories.find(e => e.getId.equals(uiFactoryId)) match {
      case None => throw new Exception("No UIFactory with id " + uiFactoryId + " found")
      case Some(uiFac) => evaluate(dpu, execPath, uiFac, input, output)
    }
  }

  /**
   * Instantiates DPUExecturo and runs the DPU
   * @see IEvaluationService
   */
  @throws(classOf[Exception])
  def evaluate(dpu: IDataProcessingUnit, execPath: URI,
    ui: UIFactory[_],
    input: MutableMap[String, InputStream],
    output: MutableMap[String, OutputStream]): ActorRef = {

    val executor = actorOf(new DPUExecutor(dpu, ui, execPath, factoryDirectory, input.toMap, output.toMap)).start
    executor ! Start()
    executor
  }

  /** bind factory service */
  def bindDirectoryService(service: IFactoryDirectory) = factoryDirectory = service

  /** unbind factory service */
  def unbindDirectoryService(service: IFactoryDirectory) = factoryDirectory = null

  /** bind UIFactory service */
  def bindUIFactory(service: UIFactory[_]) = uiFactories += service

  /** unbind UIFactory service */
  def unbindUIFactory(service: UIFactory[_]) = uiFactories -= service
}