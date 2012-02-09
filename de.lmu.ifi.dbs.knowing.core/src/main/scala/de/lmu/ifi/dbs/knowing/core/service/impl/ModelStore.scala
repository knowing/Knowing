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

import de.lmu.ifi.dbs.knowing.core.service.{ IModelProvider, IModelStore, KnowingBundleExtender }
import de.lmu.ifi.dbs.knowing.core.model.{ IProperty, INode }
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil.nodeProperties
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties.DESERIALIZE
import java.net.URL
import org.osgi.framework.{ Bundle, BundleEvent, BundleListener, BundleContext }
import org.osgi.framework.BundleEvent._
import org.osgi.service.component.ComponentContext
import org.slf4j.LoggerFactory
import scala.collection.mutable.{ HashMap, HashSet }
import scala.collection.JavaConversions._
import java.nio.file.Paths

/**
 * @author Nepomuk Seiler
 * @version 0.1
 */
class ModelStore extends IModelStore with KnowingBundleExtender {

	val log = LoggerFactory.getLogger(classOf[IModelStore])

	val MANIFEST_HEADER = "Knowing-DPU-Model"
	val RESOURCE_FOLDER = "KNOWING-INF/model"

	/** IDPUProvider services */
	lazy val serviceProviders = new HashSet[IModelProvider]

	/** Detected via Bundle Manifest Header */
	lazy val bundleProviders = new HashMap[String, URL]

	private var loadAll = true

	/**
	 * Searches for DESERIALIZE property and searches for
	 * the bundleProviders and serviceProviders for their property.value.
	 *
	 * @param node
	 * @return Some(url) or None
	 */
	def getModel(node: INode): Option[URL] = node.getProperties
		.find(_.getKey.getContent.equals(DESERIALIZE))
		.flatMap(p => getModel(resolveFilename(p)))

	/**
	 * Searches for DESERIALIZE property and searches for
	 * the bundleProviders and serviceProviders for their property.value.
	 *
	 * @param model name - IProperty.value
	 * @return Some(url) or None
	 */
	def getModel(model: String): Option[URL] = bundleProviders.get(model)
		.orElse {
			serviceProviders
				.find(_.getModels.containsKey(model))
				.map(prov => prov.getModel(model))
		}

	private def resolveFilename(property: IProperty): String = {
		val path = Paths.get(property.getValue.getContent)
		path.getFileName.toString
	}

	/*======================================*/
	/*===== Bundle Handling - Manifest =====*/
	/*======================================*/

	def onBundleInstallation(b: Bundle) = addModel(b)

	def onBundleDeinstallation(b: Bundle) = removeModel(b)

	/**
	 * Checks all bundles and adds DPUs to internal store if necessary
	 */
	def checkBundlesOnActivation(context: BundleContext) {
		for (b <- context.getBundles)
			addModel(b)
	}

	/**
	 * Add model to internal store
	 */
	def addModel(b: Bundle) = getResourceDescription(b)
		.foreach { model =>
			val entry = b.getEntry(model)
			bundleProviders.contains(model) match {
				case false if entry != null =>
					val id = model.substring(RESOURCE_FOLDER.length + 1)
					bundleProviders += (id -> entry)
					log.debug("Added Model " + model)
				case false if entry == null =>
					log.warn("Model does not exists " + model)
				case true =>
			}
		}

	/**
	 * Remove model from internal store
	 */
	def removeModel(b: Bundle) = getResourceDescription(b)
		.filter(!_.endsWith(".dpu"))
		.filter(bundleProviders.contains(_))
		.foreach { model =>
			bundleProviders -= model
			log.debug("Removed Model " + model)
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

	def bindModelProvider(provider: IModelProvider) = serviceProviders += provider

	def unbindModelProvider(provider: IModelProvider) = serviceProviders -= provider
}