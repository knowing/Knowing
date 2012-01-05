package de.lmu.ifi.dbs.knowing.core.service

import java.net.URL
import java.util.Map

/**
 * Service interface to provide DPU model resources.
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait IModelProvider {

  /**
   * 
   * @return Map with filename -> model
   */
  def getModels(): Map[String, URL]
  
  /**
   * @param name - filename of model
   * @return URI to model
   */
  def getModel(name: String): URL
  
}