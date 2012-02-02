package de.lmu.ifi.dbs.knowing.test

import de.lmu.ifi.dbs.knowing.core.service.IResourceStore
import de.lmu.ifi.dbs.knowing.core.model.INode
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties.FILE
import java.net.URL
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._

class EmbeddedResourceStore extends IResourceStore {

  lazy val resources = new HashMap[String, URL]

  def getResource(node: INode): Option[URL] = node.getProperties
    .find(_.getKey.getContent.equals(FILE))
    .flatMap(p => getResource(p.getValue.getContent))

  def getResource(resource: String): Option[URL] = resources.get(resource)

  def put(name: String, url: URL): EmbeddedResourceStore = {
    resources += (name -> url)
    this
  }
}