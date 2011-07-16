package de.lmu.ifi.dbs.knowing.core.provider
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

}

class BundleDPUProvider(bundle: Bundle, dir: String = "/KNOWING-INF") extends IDPUProvider {

  private var dpuMap: Map[String, DataProcessingUnit] = Map()
  init

  def getDataProcessingUnits: Array[DataProcessingUnit] = dpuMap.values.toArray

  def getDataProcessingUnit(name: String): DataProcessingUnit = dpuMap(name)

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
      val dpus = urls map (url => um.unmarshal(url).asInstanceOf[DataProcessingUnit])
      //TODO BundleDPUProvider => handle dpu's with identical name
      dpuMap = dpus map { dpu => (dpu.name, dpu) } toMap
    } catch {
      case e: JAXBException => e.printStackTrace
      case e: Exception => e.printStackTrace
    }
  }
}