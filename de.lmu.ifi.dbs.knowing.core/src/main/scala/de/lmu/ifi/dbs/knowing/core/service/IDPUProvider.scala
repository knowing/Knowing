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
package de.lmu.ifi.dbs.knowing.core.service

import java.net.URL
import org.osgi.framework.{ ServiceRegistration, Bundle }
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil.deserialize
import org.eclipse.sapphire.modeling.xml.{ RootXmlResource, XmlResourceStore }
import org.eclipse.sapphire.modeling.{ ResourceStoreException, UrlResourceStore }
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._
import BundleDPUProvider._

/**
 * OSGi service interface to provide DPUs stored somewhere
 * to the knowing framework.
 *
 * @author Nepomuk Seiler
 * @version 1.0
 */
trait IDPUProvider {

	/**
	 * @return all provided DPUs
	 */
	def getDataProcessingUnits: Array[IDataProcessingUnit]

	/**
	 * @return specified DPU
	 */
	def getDataProcessingUnit(name: String): Option[IDataProcessingUnit]

	/**
	 * @return url to specified DPU
	 */
	def getURL(name: String): Option[URL]

}

/**
 * Provides DPUs stored internally in a bundle. Default path is
 * \/KNOWING-INF . Searches automatically for all DPUs residing there.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
class BundleDPUProvider(bundle: Bundle, dir: String = CUSTOM_FOLDER) extends IDPUProvider {

	private val dpuMap = HashMap[String, URL]()
	init

	/**
	 *
	 */
	def getDataProcessingUnits: Array[IDataProcessingUnit] = dpuMap map { case (_, url) => deserialize(url) } toArray

	/**
	 * Doesn't handle non existing DPUs yet!
	 */
	def getDataProcessingUnit(name: String): Option[IDataProcessingUnit] = {
		dpuMap.get(name) match {
			case None => None
			case Some(url) => Some(deserialize(url))
		}
	}

	/**
	 * Doesn't handle non existing DPUs yet!
	 */
	def getURL(name: String): Option[URL] = dpuMap.get(name)

	/**
	 * reads all .dpu files in the given dir property
	 */
	private def init {
		val entries = bundle.findEntries(dir, "*.dpu", true)
		if (entries == null)
			return

		try {
			//TODO BundleDPUProvider => handle dpu's with identical name
			entries foreach { url =>
				val dpu = deserialize(url)
				dpuMap += (dpu.getName.getContent -> url)
			}
		} catch {
			case e: Exception => e.printStackTrace
		}
	}
}

/**
 * Factory methods for Java which doesn't support default values of Scala
 */
object BundleDPUProvider {

	val CUSTOM_FOLDER = "/KNOWING-INF/custom"

	def newInstance(bundle: Bundle): BundleDPUProvider = new BundleDPUProvider(bundle)
	def newInstance(bundle: Bundle, dir: String): BundleDPUProvider = new BundleDPUProvider(bundle, dir)

	def newRegisteredInstance(bundle: Bundle): ServiceRegistration[IDPUProvider] = {
		val provider = newInstance(bundle)
		bundle.getBundleContext.registerService(classOf[IDPUProvider], provider, null)
	}

	def newRegisteredInstance(bundle: Bundle, dir: String): ServiceRegistration[IDPUProvider] = {
		val provider = newInstance(bundle, dir)
		bundle.getBundleContext.registerService(classOf[IDPUProvider], provider, null)
	}
}
