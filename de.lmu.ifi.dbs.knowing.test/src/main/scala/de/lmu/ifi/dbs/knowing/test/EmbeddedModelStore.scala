package de.lmu.ifi.dbs.knowing.test

import de.lmu.ifi.dbs.knowing.core.service.IModelStore
import de.lmu.ifi.dbs.knowing.core.model.INode
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties.DESERIALIZE
import java.net.URL
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._
import de.lmu.ifi.dbs.knowing.core.model.IProperty
import java.nio.file.Paths

/**
 * @author Nepomuk Seiler
 * @version 0.1
 */
class EmbeddedModelStore extends IModelStore {

  lazy val models = new HashMap[String, URL]

  def getModel(node: INode): Option[URL] = node.getProperties
    .find(_.getKey.getContent.equals(DESERIALIZE))
    .flatMap(p => getModel(resolveFilename(p)))

  def getModel(model: String): Option[URL] = { null }

  def put(name: String, url: URL): EmbeddedModelStore = {
    models += (name -> url)
    this
  }

  private def resolveFilename(property: IProperty): String = {
    val path = Paths.get(property.getValue.getContent)
    path.getFileName.toString
  }

}