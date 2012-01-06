package de.lmu.ifi.dbs.knowing.core.service
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import java.net.URL

/**
 * Access all registerd DataProcessingUnits in the current
 * platform.
 *
 * Note: All methods should return either an ImmutableInstance
 * of the requested DPU or a copy.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait IDPUDirectory {

  //service properties
  val LOAD_ALL = "knowing.dpudirectory.loadAll"
  
  /**
   * Searches all registered dpus
   * @return Some(dpu) else None
   */
  def getDPU(id: String): Option[IDataProcessingUnit]

  /**
   * normally inside a bundle
   * @return URL to dpu
   */
  def getDPUPath(id: String): Option[URL]

  /**
   * @return all registered dpus
   */
  def getDPUs(): Array[IDataProcessingUnit]
}

