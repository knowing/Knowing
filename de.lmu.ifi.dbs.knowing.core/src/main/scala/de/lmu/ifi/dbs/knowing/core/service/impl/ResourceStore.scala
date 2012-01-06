package de.lmu.ifi.dbs.knowing.core.service.impl

import de.lmu.ifi.dbs.knowing.core.service.{ KnowingBundleExtender, IResourceStore, IResourceProvider }
import de.lmu.ifi.dbs.knowing.core.model.INode
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties.FILE

import org.osgi.framework.{ Bundle, BundleContext }
import java.net.URL
import org.slf4j.LoggerFactory
import scala.collection.mutable.{ HashMap, HashSet, ListBuffer }
import scala.collection.JavaConversions._

/**
 * This implementation currently only supports Bundle Manifest Headers.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
class ResourceStore extends IResourceStore with KnowingBundleExtender {

  val log = LoggerFactory.getLogger(classOf[IResourceStore])

  val MANIFEST_HEADER = "Knowing-DPU-resource"
  val RESOURCE_FOLDER = "KNOWING-INF/resource"

  /** IDPUProvider services */
  private lazy val serviceProviders = new HashSet[IResourceProvider]

  /** Detected via Bundle Manifest Header */
  private lazy val bundleProviders = new HashMap[String, URL]

  private var loadAll = true

  /**
   * Searches for FILE and URL property and searches for
   * the bundleProviders for their property.value.
   *
   * @param node
   * @return Some(url) or None
   */
  def getResource(node: INode): Option[URL] = {
    //    val resources = node.getProperties
    //      .filter {
    //        _.getKey.getContent match {
    //          case INodeProperties.URL | FILE => false
    //          case _ => true
    //        }
    //      }
    //
    //    resources.size match {
    //      case 0 => None
    //      case 1 => bundleProviders.get(resources(0).getValue.getContent)
    //      case _ => bundleProviders.get(resources(0).getValue.getContent)
    //    }
    node.getProperties
      .find(_.getKey.getContent.equals(FILE))
      .flatMap(p => bundleProviders.get(p.getValue.getContent))
  }

  /**
   *
   */
  def getResource(resource: String): Option[URL] = bundleProviders.get(resource)

  /*======================================*/
  /*===== Bundle Handling - Manifest =====*/
  /*======================================*/

  def checkBundlesOnActivation(context: BundleContext) = {
    for (b <- context.getBundles)
      addResource(b)
  }

  def onBundleInstallation(b: Bundle) = addResource(b)

  def onBundleDeinstallation(b: Bundle) = removeResource(b)

  /**
   * Add resource to internal store
   */
  def addResource(b: Bundle) = getResourceDescription(b)
    .foreach { res =>
      val entry = b.getEntry(res)
      bundleProviders.contains(res) match {
        case false if entry != null =>
          bundleProviders += (res -> entry)
          log.debug("Added Resource " + res)
        case false if entry == null =>
          log.warn("Resource does not exists " + res)
        case true =>
      }
    }

  /**
   * Remove resource from internal store
   */
  def removeResource(b: Bundle) = getResourceDescription(b)
    .filter(bundleProviders.contains(_))
    .foreach { res =>
      bundleProviders -= res
      log.debug("Removed Resource " + res)
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

  def bindResourceProvider(provider: IResourceProvider) = serviceProviders += provider

  def unbindResourceProvider(provider: IResourceProvider) = serviceProviders -= provider

}