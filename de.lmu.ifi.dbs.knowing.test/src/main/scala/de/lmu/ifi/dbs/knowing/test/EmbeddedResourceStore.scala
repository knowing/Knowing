package de.lmu.ifi.dbs.knowing.test

import de.lmu.ifi.dbs.knowing.core.service.IResourceStore
import de.lmu.ifi.dbs.knowing.core.model.INode
import java.net.URL

class EmbeddedResourceStore extends IResourceStore {

  def getResource(node: INode): Option[URL] = { null }

  def getResource(resource: String): Option[URL] = { null }

}