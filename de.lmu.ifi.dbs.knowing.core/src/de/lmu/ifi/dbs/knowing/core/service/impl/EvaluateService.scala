package de.lmu.ifi.dbs.knowing.core.service.impl

import java.net.URI
import java.io.{InputStream,OutputStream}
import akka.actor.Actor.actorOf
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.processing.DPUExecutor
import de.lmu.ifi.dbs.knowing.core.service.{IEvaluateService, IFactoryDirectory}
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil

/**
 * Default implementation for the EvaluationService
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
class EvaluateService extends IEvaluateService {

  /** 1..1 relation */
  private var factoryDirectory: IFactoryDirectory = _
  
  /**
   * Instantiates GraphSupervisor and runs the DPU
   * @see IEvaluationService
   */
  def evaluate(dpu: IDataProcessingUnit, ui: UIFactory, execPath: URI): ActorRef = evaluate(dpu, ui, execPath, Map(), Map())
  
  def evaluate(dpu: IDataProcessingUnit, ui: UIFactory, execPath: URI, input: Map[String, InputStream], output: Map[String, OutputStream]): ActorRef = {
    val executor = actorOf(new DPUExecutor(dpu,ui, execPath, factoryDirectory)).start
    executor ! Start()
    executor
  }

  /** bind factory service */
  def bindDirectoryService(service: IFactoryDirectory) = factoryDirectory = service
  
  /** unbind factory service */
  def unbindDirectoryService(service: IFactoryDirectory) = factoryDirectory = null
}