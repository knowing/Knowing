package de.lmu.ifi.dbs.knowing.core.service

import java.net.URL
import org.osgi.framework.Bundle
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.util.DPUUtil.copy
import org.eclipse.sapphire.modeling.xml.{RootXmlResource,XmlResourceStore}
import org.eclipse.sapphire.modeling.{ResourceStoreException,UrlResourceStore}

/**
 * OSGi service interface to provide DPUs stored somewhere
 * to the knowing framework.
 * 
 * @Nepomuk Seiler
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
class BundleDPUProvider(bundle: Bundle, dir: String = "/KNOWING-INF") extends IDPUProvider {

  private var dpuMap: Map[String, (IDataProcessingUnit, URL)] = Map()
  init

  /**
   *
   */
  def getDataProcessingUnits: Array[IDataProcessingUnit] = dpuMap map { case (_, (dpu, _)) => dpu } toArray

  /**
   * Doesn't handle non existing DPUs yet!
   */
  def getDataProcessingUnit(name: String): Option[IDataProcessingUnit] = {
    dpuMap.get(name) match {
      case None => None
      case Some(e) => Some(e._1)
    }
  }

  /**
   * Doesn't handle non existing DPUs yet!
   */
  def getURL(name: String): Option[URL] = {
    dpuMap.get(name) match {
      case None => None
      case Some(entry) => Some(entry._2)
    }
  }

  /**
   * reads all .dpu files in the given dir property
   */
  private def init {
    val entries = bundle.findEntries(dir, "*.dpu", true)
    if (entries == null)
      return

    var urls: List[URL] = Nil
    while (entries.hasMoreElements)
      urls = entries.nextElement.asInstanceOf[URL] :: urls

    try {
      val dpus = urls map { url =>
        val store = new XmlResourceStore(new UrlResourceStore(url))
        val resource = new RootXmlResource(store)
        val dpu: IDataProcessingUnit = IDataProcessingUnit.TYPE.instantiate(resource)
        (copy(dpu), url)
      }
      //TODO BundleDPUProvider => handle dpu's with identical name
      dpuMap = dpus map { case (dpu, url) => (dpu.getName.getContent, (dpu, url)) } toMap
    } catch {
      case e: ResourceStoreException => e.printStackTrace
      case e: Exception => e.printStackTrace
    }
  }
}

/**
 * Factory methods for Java which doesn't support default values of Scala
 */
object BundleDPUProvider {

  def newInstance(bundle: Bundle): BundleDPUProvider = new BundleDPUProvider(bundle)
  def newInstance(bundle: Bundle, dir: String): BundleDPUProvider = new BundleDPUProvider(bundle, dir)
}
