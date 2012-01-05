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
import IDPUDirectory._

/**
 * Standard implementation of a DPU-Directory service.
 *
 * @author Nepomuk Seiler
 * @version 1.0
 * @since 2011-09-22
 */
class DPUDirectory extends IDPUDirectory with SynchronousBundleListener {

  private lazy val log = LoggerFactory.getLogger(classOf[IDPUDirectory])

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

  /**
   * @see https://github.com/gkvas/gemini.jpa/blob/master/org.eclipse.gemini.jpa/src/org/eclipse/gemini/jpa/PersistenceBundleExtender.java
   */
  def bundleChanged(event: BundleEvent) = event.getType match {
    case STARTING | STARTED | RESOLVED | INSTALLED | LAZY_ACTIVATION =>
      addDataProcessingUnits(event.getBundle)

    case STOPPING | STOPPED | UNRESOLVED | UNINSTALLED =>
      removeDataProcessingUnits(event.getBundle)
    case _ =>
  }

  /**
   * Checks all bundles and adds DPUs to internal store if necessary
   */
  def checkAllBundles(context: BundleContext) {
    for (b <- context.getBundles)
      addDataProcessingUnits(b)
  }

  /**
   * Add DPU to internal store
   */
  def addDataProcessingUnits(b: Bundle) = getDataProcessingUnitDesc(b)
    .filter(_.endsWith(".dpu"))
    .foreach { dpu =>
      val entry = b.getEntry(KNOWING_FOLDER + "/" + dpu)
      bundleProviders.contains(dpu) match {
        case false if entry != null =>
          bundleProviders += (dpu -> entry)
          log.debug("Added DPU " + dpu + " with url " + entry)
        case false if entry == null =>
          log.warn("DPU does not exists " + dpu)
        case true =>
      }
    }

  /**
   * Remove DPU from internal store
   */
  def removeDataProcessingUnits(b: Bundle) = getDataProcessingUnitDesc(b)
    .filter(_.endsWith(".dpu"))
    .foreach { dpu =>
      bundleProviders.contains(dpu) match {
        case true =>
          bundleProviders -= dpu
          log.debug("Removed DPU " + dpu)
        case false =>
      }
    }

  /**
   * Resolve DPUs from KNOWING-INF.
   */
  def getDataProcessingUnitDesc(b: Bundle): List[String] = {
    val entries = b.getEntryPaths(KNOWING_FOLDER)
    val convert = (entries: java.util.Enumeration[String]) => {
      //Remove KNOWING-INF/ from string
      entries.map(e => e.substring(KNOWING_FOLDER.length + 1)).toList
    }

    b.getHeaders.get(KNOWING_DPU_MANIFEST_HEADER) match {
      case null =>
        (b.getEntryPaths(KNOWING_FOLDER), loadAll) match {
          case (null, _) =>
            List()
          case (entries, false) =>
            log.warn("Bundle " + b.getSymbolicName + " has DPUs, but doesnt export them. Add '" + KNOWING_DPU_MANIFEST_HEADER + "': *' to MANIFEST.MF or set service to loadAll=true")
            List()
          case (entries, true) =>
            convert(entries)
        }

      case DPU_WILDCARD => convert(entries)
      case header => header.split(DPU_SEPARATOR).toList
    }
  }

  /*======================================*/
  /*====== Activation / Deactivation =====*/
  /*======================================*/

  def activate(context: ComponentContext, properties: java.util.Map[String, Object]) {
    loadAll = properties.get(LOAD_ALL).asInstanceOf[Boolean]
    context.getBundleContext.addBundleListener(this)
    checkAllBundles(context.getBundleContext)
    log.debug("DPUDirectory service activated. LoadAll = " + loadAll)
  }

  def deactivate(context: ComponentContext) {
    context.getBundleContext.removeBundleListener(this)
    log.debug("DPUDirectory service deactivated")
  }

  //TODO check if this implementation works properly
  def modified(context: ComponentContext, properties: java.util.Map[String, Object]) {
    bundleProviders.clear
    loadAll = properties.get(LOAD_ALL).asInstanceOf[Boolean]
    checkAllBundles(context.getBundleContext)
    log.debug("DPUDirectory service modified. LoadAll = " + loadAll)
  }

  /* ======================= */
  /* ==== Bind services ==== */
  /* ======================= */

  def bindDPUProvider(provider: IDPUProvider) = serviceProviders += provider

  def unbindDPUProvider(provider: IDPUProvider) = serviceProviders -= provider

}