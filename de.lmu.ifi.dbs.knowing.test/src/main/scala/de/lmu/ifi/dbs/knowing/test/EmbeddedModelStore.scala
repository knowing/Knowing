package de.lmu.ifi.dbs.knowing.test

import de.lmu.ifi.dbs.knowing.core.service.IModelStore
import de.lmu.ifi.dbs.knowing.core.model.INode
import java.net.URL
import scala.collection.mutable.HashMap

/**
 * @author Nepomuk Seiler
 * @version 0.1
 */
class EmbeddedModelStore extends IModelStore {

  lazy val models = new HashMap[String, URL]
  
  def getModel(node: INode): Option[URL] = { null }

  def getModel(model: String): Option[URL] = { null }
  
  /**
   * fluent interface
   */
  def put(name: String, url: URL): EmbeddedModelStore = {
    models += (name -> url)
    this
  }

}