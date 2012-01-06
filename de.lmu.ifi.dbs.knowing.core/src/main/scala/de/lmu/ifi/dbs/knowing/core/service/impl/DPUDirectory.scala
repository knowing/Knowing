package de.lmu.ifi.dbs.knowing.core.service.impl

import de.lmu.ifi.dbs.knowing.core.service._
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil.{ copy, deserialize }
import scala.collection.mutable.{ HashMap, HashSet, ListBuffer }
import scala.collection.JavaConversions._
import java.net.URL
import org.osgi.service.component.ComponentContext
import org.osgi.framework.{ Bundle, BundleEvent, BundleContext, SynchronousBundleListener }
import org.osgi.framework.BundleEvent._
import org.slf4j.LoggerFactory

/**
 * Standard implementation of a DPU-Directory service.
 *
 * @author Nepomuk Seiler
 * @version 1.0
 * @since 2011-09-22
 */
class DPUDirectory extends IDPUDirectory with KnowingBundleExtender {

  val log = LoggerFactory.getLogger(classOf[IDPUDirectory])

  val MANIFEST_HEADER = "Knowing-DPU"
  val RESOURCE_FOLDER = "KNOWING-INF/dpu"

  /** IDPUProvider services */
  private lazy val serviceProviders = new HashSet[IDPUProvider]

  /** Detected via Bundle Manifest Header */
  private lazy val bundleProviders = new HashMap[String, URL]

  private var loadAll = true

  /**
   * @param dpu id
   * @return dpu first found with id
   */
  def getDPU(id: String): Option[IDataProcessingUnit] = {
    bundleProviders.get(id) match {
      case Some(e) => return Some(deserialize(e))
      case None =>
    }

    val results = new ListBuffer[IDataProcessingUnit]
    for (provider <- serviceProviders) {
      provider.getDataProcessingUnit(id) match {
        case None =>
        case Some(dpu) => results += dpu
      }
    }
    results.headOption
  }

  /**
   * This method returns a copy of the original DPU.
   *
   * @param dpu id
   * @return dpu-url first found with id
   */
  def getDPUPath(id: String): Option[URL] = {
    val results = new ListBuffer[URL]
    for (provider <- serviceProviders) {
      provider.getURL(id) match {
        case None =>
        case Some(url) => results += url
      }
    }
    results.headOption
  }

  /**
   *
   */
  def getDPUs(): Array[IDataProcessingUnit] = {
    val dpuArrays = serviceProviders map (_.getDataProcessingUnits.toList)
    dpuArrays.reduceLeft((head, next) => head ::: next).toArray
  }

  /*======================================*/
  /*===== Bundle Handling - Manifest =====*/
  /*======================================*/

  def onBundleInstallation(b: Bundle) = addDataProcessingUnits(b)

  def onBundleDeinstallation(b: Bundle) = removeDataProcessingUnits(b)

  /**
   * Checks all bundles and adds DPUs to internal store if necessary
   */
  def checkBundlesOnActivation(context: BundleContext) {
    for (b <- context.getBundles)
      addDataProcessingUnits(b)
  }

  /**
   * Add DPU to internal store
   */
  def addDataProcessingUnits(b: Bundle) = getResourceDescription(b, loadAll)
    .filter(_.endsWith(".dpu"))
    .foreach { dpu =>
      val entry = b.getEntry(dpu)
      bundleProviders.contains(dpu) match {
        case false if entry != null =>
          bundleProviders += (dpu -> entry)
          log.debug("Added DPU " + dpu)
        case false if entry == null =>
          log.warn("DPU does not exists " + dpu)
        case true =>
      }
    }

  /**
   * Remove DPU from internal store
   */
  def removeDataProcessingUnits(b: Bundle) = getResourceDescription(b, loadAll)
    .filter(_.endsWith(".dpu"))
    .filter(bundleProviders.contains(_))
    .foreach { dpu =>
      bundleProviders -= dpu
      log.debug("Removed DPU " + dpu)
    }

  /*======================================*/
  /*====== Activation / Deactivation =====*/
  /*======================================*/

  def configure(properties: java.util.Map[String, Object]) {
    loadAll = properties.get(LOAD_ALL).asInstanceOf[Boolean]
  }

  /* ======================= */
  /* ==== Bind services ==== */
  /* ======================= */

  def bindDPUProvider(provider: IDPUProvider) = serviceProviders += provider

  def unbindDPUProvider(provider: IDPUProvider) = serviceProviders -= provider

}