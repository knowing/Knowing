package de.lmu.ifi.dbs.knowing.test

import de.lmu.ifi.dbs.knowing.core.service.IModelStore
import de.lmu.ifi.dbs.knowing.core.model.INode
import java.net.URL

class EmbeddedModelStore extends IModelStore {

  def getModel(node: INode): Option[URL] = { null }

  def getModel(model: String): Option[URL] = { null }

}