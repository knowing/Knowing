package de.lmu.ifi.dbs.knowing.test

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.service._
import de.lmu.ifi.dbs.knowing.core.processing.DPUExecutor
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil.deserialize
import org.eclipse.sapphire.modeling.xml.{ RootXmlResource, XmlResourceStore }
import org.eclipse.sapphire.modeling.UrlResourceStore
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import java.net.{ URI, URL }
import weka.core.{ Attribute, Instances }
import org.scalatest.matchers._

/**
 * Mixin with scalatest.org test to reduce boilerplate code.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait KnowingTestKit extends EventMatchers {

  /* ===================================== */
  /* ==== DataProcessingUnit Loading ===== */
  /* ===================================== */

  /**
   * @param url - URL to DPU
   * @return dpu - copy of the original dpu
   */
  def loadDPU(url: URL): IDataProcessingUnit = deserialize(url)

  /**
   * DPU is resolved relative to the test class location
   *
   * @param name - name of the DPU
   * @return dpu - copy of the original dpu
   */
  def loadDPU(name: String): IDataProcessingUnit = {
    //TODO implement different search paths
    val url = getClass.getResource(name)
    loadDPU(url)
  }

  /* ===================================== */
  /* ============ DPUExecutor  =========== */
  /* ===================================== */

  def createDPUExecutor(dpu: IDataProcessingUnit,
    uiFactory: UIFactory[_],
    exePath: URI,
    factoryDirectory: IFactoryDirectory,
    modelStore: IModelStore,
    resourceStore: IResourceStore): ActorRef = {
    actorOf(new DPUExecutor(dpu, uiFactory, exePath, factoryDirectory, modelStore, resourceStore))
  }

  /* ===================================== */
  /* ======== Matching Instances  ======== */
  /* ===================================== */

}
