package de.lmu.ifi.dbs.knowing.core.service
import java.net.URL

import org.osgi.framework.Bundle

import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException

trait IDPUProvider {

  def getDataProcessingUnits: Array[DataProcessingUnit]

  def getDataProcessingUnit(name: String): DataProcessingUnit
  
  def getURL(name: String): URL

}

class BundleDPUProvider(bundle: Bundle, dir: String = "/KNOWING-INF") extends IDPUProvider {

  private var dpuMap: Map[String, (DataProcessingUnit, URL)] = Map()
  init

  /**
   * 
   */
  def getDataProcessingUnits: Array[DataProcessingUnit] = dpuMap map { case (_, (dpu, _)) => dpu } toArray

  /**
   * Doesn't handle non existing DPUs yet!
   */
  def getDataProcessingUnit(name: String): DataProcessingUnit = {
    dpuMap.get(name) match {
      case None => null
      case Some(e) => e._1
    }
  }
  
  /**
   * Doesn't handle non existing DPUs yet!
   */
  def getURL(name: String): URL = dpuMap(name)._2

  private def init {
    val entries = bundle.findEntries(dir, "*.dpu", true)
    if (entries == null)
      return

      
    var urls: List[URL] = Nil
    while (entries.hasMoreElements)
      urls = entries.nextElement.asInstanceOf[URL] :: urls

    try {
      val context = JAXBContext.newInstance(classOf[DataProcessingUnit])
      val um = context.createUnmarshaller
      val dpus = urls map (url => (um.unmarshal(url).asInstanceOf[DataProcessingUnit], url))
      //TODO BundleDPUProvider => handle dpu's with identical name
      dpuMap = dpus map {case (dpu, url) => (dpu.name, (dpu, url))} toMap
    } catch {
      case e: JAXBException => e.printStackTrace
      case e: Exception => e.printStackTrace
    }
  }
}

object BundleDPUProvider {
  
  def newInstance(bundle: Bundle): BundleDPUProvider = new BundleDPUProvider(bundle)
}