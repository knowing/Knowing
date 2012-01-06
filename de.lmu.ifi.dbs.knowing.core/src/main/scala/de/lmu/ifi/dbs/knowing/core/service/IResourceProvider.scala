package de.lmu.ifi.dbs.knowing.core.service
import java.net.URL

/**
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait IResourceProvider {

  /**
   *
   * @return Map with filename -> model
   */
  def getResources(): Map[String, URL]

  /**
   * @param name - filename of model
   * @return URI to model
   */
  def getResource(name: String): URL

}