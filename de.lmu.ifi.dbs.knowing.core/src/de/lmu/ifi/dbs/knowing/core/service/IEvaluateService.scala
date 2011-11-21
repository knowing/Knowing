package de.lmu.ifi.dbs.knowing.core.service

import java.net.URI
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit

/**
 * OSGi service responsible for starting a data mining process.
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait IEvaluateService {

  /**
   * Executes the given dpu
   * 
   * @param dpu - the DataProcessingUnit
   * @param uiFactory - choose uiSystem and where to present
   * @param execPath - executionPath to resolve relative properties
   */
  def evaluate(dpu: IDataProcessingUnit, ui: UIFactory, execPath: URI): ActorRef
}