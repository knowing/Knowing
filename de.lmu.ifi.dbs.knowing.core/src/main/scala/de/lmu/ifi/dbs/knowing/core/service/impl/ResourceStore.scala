/*																*\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|	**
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,	**
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|	**
** 																**
** Knowing Framework											**
** Apache License - http://www.apache.org/licenses/				**
** LMU Munich - Database Systems Group							**
** http://www.dbs.ifi.lmu.de/									**
\*																*/
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
	lazy val serviceProviders = new HashSet[IResourceProvider]

	/** Detected via Bundle Manifest Header */
	lazy val bundleProviders = new HashMap[String, URL]

	private var loadAll = true

	/**
	 * Searches for FILE property and searches for
	 * the bundleProviders and serviceProviders  for their property.value.
	 *
	 * @param node
	 * @return Some(url) or None
	 */
	def getResource(node: INode): Option[URL] = node.getProperties
		.find(_.getKey.getContent.equals(FILE))
		.flatMap(p => getResource(p.getValue.getContent))

	/**
	 * Searches for FILE property and searches for
	 * the bundleProviders and serviceProviders for their property.value.
	 *
	 * @param model name - IProperty.value
	 * @return Some(url) or None
	 */
	def getResource(resource: String): Option[URL] = bundleProviders.get(resource)
		.orElse {
			serviceProviders
				.find(_.getResources.containsKey(resource))
				.map(prov => prov.getResource(resource))
		}

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
					val id = res.substring(RESOURCE_FOLDER.length + 1)
					bundleProviders += (id -> entry)
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