package de.lmu.ifi.dbs.knowing.core.service.impl

import de.lmu.ifi.dbs.knowing.core.service.{ IModelProvider, IModelStore }
import de.lmu.ifi.dbs.knowing.core.service.IModelStore._
import de.lmu.ifi.dbs.knowing.core.model.INode
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil.nodeProperties
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties
import java.net.URL
import org.osgi.framework.{ Bundle, BundleEvent, BundleListener, BundleContext }
import org.osgi.framework.BundleEvent._
import org.osgi.service.component.ComponentContext
import org.slf4j.LoggerFactory
import scala.collection.mutable.{ HashMap, HashSet }
import scala.collection.JavaConversions._

/**
 * @author Nepomuk Seiler
 * @version 0.1
 */
class ModelStore extends IModelStore with BundleListener {

  private lazy val log = LoggerFactory.getLogger(classOf[IModelStore])

  /** IDPUProvider services */
  private lazy val serviceProviders = new HashSet[IModelProvider]

  /** Detected via Bundle Manifest Header */
  private lazy val bundleProviders = new HashMap[String, URL]

  private var loadAll = true

  /**
   * This current implementation only respects Bundle Manifest Headers
   */
  def getModel(node: INode): Option[URL] = {
    node.getProperties
      .find(p => p.getKey.getContent.equals(INodeProperties.DESERIALIZE))
      .flatMap(p => bundleProviders.get(p.getValue.getContent))
  }

  /**
   * This current implementation only respects Bundle Manifest Headers
   */
  def getModel(model: String): Option[URL] = bundleProviders.get(model)

  /*======================================*/
  /*===== Bundle Handling - Manifest =====*/
  /*======================================*/

  /**
   * @see https://github.com/gkvas/gemini.jpa/blob/master/org.eclipse.gemini.jpa/src/org/eclipse/gemini/jpa/PersistenceBundleExtender.java
   */
  def bundleChanged(event: BundleEvent) = event.getType match {
    case STARTING | STARTED | RESOLVED | INSTALLED | LAZY_ACTIVATION =>
      addModel(event.getBundle)
    case STOPPING | STOPPED | UNRESOLVED | UNINSTALLED =>
      removeModel(event.getBundle)
    case _ =>
  }

  /**
   * Checks all bundles and adds DPUs to internal store if necessary
   */
  def checkAllBundles(context: BundleContext) {
    for (b <- context.getBundles)
      addModel(b)
  }

  /**
   * Add DPU to internal store
   */
  def addModel(b: Bundle) = getModelDesc(b)
    .filter(!_.endsWith(".dpu"))
    .foreach { model =>
      val entry = b.getEntry(KNOWING_FOLDER + "/" + model)
      bundleProviders.contains(model) match {
        case false if entry != null =>
          bundleProviders += (model -> entry)
          log.debug("Added Model " + model + " with url " + entry)
        case false if entry == null =>
          log.warn("Model does not exists " + model)
        case true =>
      }
    }

  /**
   * Remove DPU from internal store
   */
  def removeModel(b: Bundle) = getModelDesc(b)
    .filter(!_.endsWith(".dpu"))
    .foreach { model =>
      bundleProviders.contains(model) match {
        case true =>
          bundleProviders -= model
          log.debug("Removed Model " + model)
        case false =>
      }
    }

  /**
   * Resolve Models from KNOWING-INF.
   */
  def getModelDesc(b: Bundle): List[String] = {
    val entries = b.getEntryPaths(KNOWING_FOLDER)
    val convert = (entries: java.util.Enumeration[String]) => {
      //Remove KNOWING-INF/ from string
      entries.map(e => e.substring(KNOWING_FOLDER.length + 1)).toList
    }

    b.getHeaders.get(KNOWING_MODEL_MANIFEST_HEADER) match {
      case null =>
        (b.getEntryPaths(KNOWING_FOLDER), loadAll) match {
          case (null, _) =>
            List()
          case (entries, false) =>
            log.warn("Bundle " + b.getSymbolicName + " has Models, but doesnt export them. Add '" + KNOWING_MODEL_MANIFEST_HEADER + "': *' to MANIFEST.MF or set service to loadAll=true")
            List()
          case (entries, true) =>
            convert(entries)
        }

      case MODEL_WILDCARD => convert(entries)
      case header => header.split(MODEL_SEPARATOR).toList
    }
  }

  /*======================================*/
  /*====== Activation / Deactivation =====*/
  /*======================================*/

  def activate(context: ComponentContext, properties: java.util.Map[String, Object]) {
    loadAll = properties.get(LOAD_ALL).asInstanceOf[Boolean]
    context.getBundleContext.addBundleListener(this)
    checkAllBundles(context.getBundleContext)
    log.debug("ModelStore service activated. LoadAll = " + loadAll)
  }

  def deactivate(context: ComponentContext) {
    context.getBundleContext.removeBundleListener(this)
    log.debug("ModelStore service deactivated")
  }

  //TODO check if this implementation works properly
  def modified(context: ComponentContext, properties: java.util.Map[String, Object]) {
    bundleProviders.clear
    loadAll = properties.get(LOAD_ALL).asInstanceOf[Boolean]
    checkAllBundles(context.getBundleContext)
    log.debug("ModelStore service modified. LoadAll = " + loadAll)
  }

  /* ======================= */
  /* ==== Bind services ==== */
  /* ======================= */

  def bindModelProvider(provider: IModelProvider) = serviceProviders += provider

  def unbindModelProvider(provider: IModelProvider) = serviceProviders -= provider
}