package de.lmu.ifi.dbs.knowing.core.service.impl

import de.lmu.ifi.dbs.knowing.core.service._
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import scala.collection.mutable.HashSet
import java.net.URL

/**
 * Standard implementation of a DPU-Directory service.
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 * @since 2011-09-22
 */
class DPUDirectory extends IDPUDirectory {

  private lazy val providers = new HashSet[IDPUProvider]
  private val optionFilter:(Option[_] => Boolean) = {
    case None => false
    case Some(_) => true
  }
  
  /**
   * @param dpu id
   * @return dpu first found with id
   */
  def getDPU(id: String): Option[IDataProcessingUnit] = {
    val ret = providers map {
      _.getDataProcessingUnit(id) match {
        case None => None
        case Some(dpu) => Some(dpu)
      }
    }
    ret.filter(optionFilter).headOption getOrElse (None)
  }

  /**
   * @param dpu id
   * @return dpu-url first found with id
   */
  def getDPUPath(id: String): Option[URL] = {
    val ret = providers map {
      _.getURL(id) match {
        case None => None
        case Some(url) => Some(url)
      }
    }
    ret.filter(optionFilter).headOption getOrElse (None)
  }

  /**
   * 
   */
  def getDPUs(): Array[IDataProcessingUnit] = {
    val dpuArrays = providers map (_.getDataProcessingUnits.toList)
    dpuArrays.reduceLeft((head, next) => head ::: next).toArray
  }

  /* ======================= */
  /* ==== Bind services ==== */
  /* ======================= */
  
  def bindDPUProvider(provider: IDPUProvider) = providers += provider

  def unbindDPUProvider(provider: IDPUProvider) = providers -= provider

}