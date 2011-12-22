package de.lmu.ifi.dbs.knowing.core.service

import java.net.URI
import akka.actor.ActorRef
import java.io.{InputStream,OutputStream}
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import scala.collection.mutable.{Map => MutableMap}

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
  def evaluate(dpu: IDataProcessingUnit, ui: UIFactory[_], execPath: URI): ActorRef
  
  def evaluate(dpu: IDataProcessingUnit, ui: UIFactory[_], execPath: URI, input: MutableMap[String, InputStream], output: MutableMap[String, OutputStream]): ActorRef
}