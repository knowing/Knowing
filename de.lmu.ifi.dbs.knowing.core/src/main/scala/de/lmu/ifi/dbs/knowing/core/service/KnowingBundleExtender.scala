package de.lmu.ifi.dbs.knowing.core.service

import org.osgi.framework.{ Bundle, BundleContext, BundleEvent, SynchronousBundleListener }
import org.osgi.framework.BundleEvent._
import org.osgi.service.component.ComponentContext
import org.slf4j.Logger

import scala.collection.JavaConversions._

trait KnowingBundleExtender extends SynchronousBundleListener {

  val log: Logger

  val MANIFEST_HEADER: String
  val RESOURCE_FOLDER: String
  val WILDCARD = "*"
  val SEPARATOR = ","

  /**
   * This default implementation currentely just differs between
   * Bundle starting (Starting, Started, Resolved, Installed, LazyActivaion) or
   * Bundle stoping  (Stopping, Stopped, Unresolved, Uninstalled)
   *
   * @see https://github.com/gkvas/gemini.jpa/blob/master/org.eclipse.gemini.jpa/src/org/eclipse/gemini/jpa/PersistenceBundleExtender.java
   *
   */
  def bundleChanged(event: BundleEvent) = event.getType match {
    case STARTING | STARTED | RESOLVED | INSTALLED | LAZY_ACTIVATION =>
      onBundleInstallation(event.getBundle)
    case STOPPING | STOPPED | UNRESOLVED | UNINSTALLED =>
      onBundleDeinstallation(event.getBundle)
    case _ =>
  }

  /**
   * Configures the service on activation and modification
   */
  def configure(properties: java.util.Map[String, Object])

  def checkBundlesOnActivation(context: BundleContext)

  def onBundleInstallation(b: Bundle)

  def onBundleDeinstallation(b: Bundle)

  def getResourceDescription(b: Bundle, loadAll: Boolean = true): List[String] = {
    val entries = b.getEntryPaths(RESOURCE_FOLDER)

    (b.getHeaders.get(MANIFEST_HEADER), entries) match {

      //No manifest header and no entries
      case (null, null) => List()

      //Manifest header defined, but no entries!
      case (header, null) =>
        log.warn("Bundle " + b.getSymbolicName + " has NO files, but exports [" + header + "] Remove '" + MANIFEST_HEADER + " or add files")
        List()

      //No Manifest header, but entries founds
      case (null, _) =>
        (b.getEntryPaths(RESOURCE_FOLDER), loadAll) match {
          case (null, _) =>
            List()
          case (entries, false) =>
            log.warn("Bundle " + b.getSymbolicName + " has files, but doesnt exports them. Add '" + MANIFEST_HEADER + "': *' to MANIFEST.MF or set service to loadAll=true")
            List()
          case (entries, true) => entries.toList
        }

      case (WILDCARD, _) => entries.toList
      case (header, _) => header.split(SEPARATOR).map(RESOURCE_FOLDER + "/" + _).toList
    }
  }

  /*======================================*/
  /*====== Activation / Deactivation =====*/
  /*======================================*/

  def activate(context: ComponentContext, properties: java.util.Map[String, Object]) {
    context.getBundleContext.addBundleListener(this)
    checkBundlesOnActivation(context.getBundleContext)
    log.debug(properties.get("component.name") + " service activated")
  }

  def deactivate(context: ComponentContext, properties: java.util.Map[String, Object]) {
    context.getBundleContext.removeBundleListener(this)
    log.debug(properties.get("component.name") + " service deactivated")
  }

  //TODO check if this implementation works properly
  def modified(context: ComponentContext, properties: java.util.Map[String, Object]) {
    configure(properties)
    checkBundlesOnActivation(context.getBundleContext)
    log.debug(properties.get("component.name") + " service modified")
  }

}