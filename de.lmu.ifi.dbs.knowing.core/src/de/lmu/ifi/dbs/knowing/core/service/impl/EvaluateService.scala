package de.lmu.ifi.dbs.knowing.core.service.impl

import java.net.URI
import akka.actor.Actor.actorOf
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.events.Start
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.processing.GraphSupervisor
import de.lmu.ifi.dbs.knowing.core.service.IEvaluateService
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil


class EvaluateService extends IEvaluateService {

  private var factoryDirectory: IFactoryDirectory = _
  
  def evaluate(dpu: IDataProcessingUnit, ui: UIFactory, execPath: URI): ActorRef = {
    val supervisor = actorOf(new GraphSupervisor(dpu,ui, execPath, factoryDirectory)).start
    supervisor ! Start()
    supervisor
  }

  def bindDirectoryService(service: IFactoryDirectory) = factoryDirectory = service
  
  def unbindDirectoryService(service: IFactoryDirectory) = factoryDirectory = null
}