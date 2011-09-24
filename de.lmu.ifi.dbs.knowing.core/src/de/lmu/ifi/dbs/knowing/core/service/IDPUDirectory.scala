package de.lmu.ifi.dbs.knowing.core.service
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import java.net.URL

trait IDPUDirectory {

  def getDPU(id: String): Option[IDataProcessingUnit]
  
  def getDPUPath(id: String): Option[URL]

  def getDPUs(): Array[IDataProcessingUnit]
}
