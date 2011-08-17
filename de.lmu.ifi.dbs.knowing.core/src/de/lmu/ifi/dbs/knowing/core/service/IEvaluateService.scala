package de.lmu.ifi.dbs.knowing.core.service

import java.net.URI

import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit

trait IEvaluateService {

  def evaluate(dpu: DataProcessingUnit, ui: UIFactory, execPath: URI): ActorRef
}