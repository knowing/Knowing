package de.lmu.ifi.dbs.knowing.core.service
import java.net.URL
import org.osgi.framework.Bundle
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import org.eclipse.sapphire.modeling.xml.XmlResourceStore
import org.eclipse.sapphire.modeling.UrlResourceStore
import org.eclipse.sapphire.modeling.ResourceStoreException
import org.eclipse.sapphire.modeling.xml.RootXmlResource

trait IDPUProvider {

  def getDataProcessingUnits: Array[IDataProcessingUnit]

  def getDataProcessingUnit(name: String): IDataProcessingUnit

  def getURL(name: String): URL

}

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
  def getDataProcessingUnit(name: String): IDataProcessingUnit = {
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
      val dpus = urls map { url =>
        val store = new XmlResourceStore(new UrlResourceStore(url));
        val resource = new RootXmlResource(store)
        val dpu: IDataProcessingUnit = IDataProcessingUnit.TYPE.instantiate(resource)
        (dpu, url)
      }
      //TODO BundleDPUProvider => handle dpu's with identical name
      dpuMap = dpus map { case (dpu, url) => (dpu.getName.getContent, (dpu, url)) } toMap
    } catch {
      case e: ResourceStoreException => e.printStackTrace
      case e: Exception => e.printStackTrace
    }
  }
}

object BundleDPUProvider {

  def newInstance(bundle: Bundle): BundleDPUProvider = new BundleDPUProvider(bundle)
  def newInstance(bundle: Bundle, dir: String): BundleDPUProvider = new BundleDPUProvider(bundle, dir)
}
