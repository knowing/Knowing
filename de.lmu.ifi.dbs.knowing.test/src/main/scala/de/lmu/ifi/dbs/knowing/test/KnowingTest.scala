package de.lmu.ifi.dbs.knowing.test

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.processing.DPUExecutor
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import org.eclipse.sapphire.modeling.xml.{ RootXmlResource, XmlResourceStore }
import org.eclipse.sapphire.modeling.UrlResourceStore
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import java.net.{ URI, URL }

/**
 * Mixin with scalatest.org test to reduce boilerplate code.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait KnowingTest {

  /* ===================================== */
  /* ==== DataProcessingUnit Loading ===== */
  /* ===================================== */

  /**
   * @param url - URL to DPU
   * @return dpu - copy of the original dpu
   */
  def loadDPU(url: URL): IDataProcessingUnit = {
    val store = new XmlResourceStore(new UrlResourceStore(url))
    val resource = new RootXmlResource(store)
    val orgDPU: IDataProcessingUnit = IDataProcessingUnit.TYPE.instantiate(resource)
    val dpu: IDataProcessingUnit = IDataProcessingUnit.TYPE.instantiate()
    dpu.copy(orgDPU)
    dpu
  }

  /**
   * DPU is resolved relative to the test class location
   *
   * @param name - name of the DPU
   * @return dpu - copy of the original dpu
   */
  def loadDPU(name: String): IDataProcessingUnit = {
    val url = getClass.getResource(name)
    loadDPU(url)
  }

  /* ===================================== */
  /* ============ DPUExecutor  =========== */
  /* ===================================== */

  def createDPUExecutor(dpu: IDataProcessingUnit, uiFactory: UIFactory[_], exePath: URI, factoryDirectory: IFactoryDirectory): ActorRef = {
    actorOf(new DPUExecutor(dpu, uiFactory, exePath, factoryDirectory))
  }

}