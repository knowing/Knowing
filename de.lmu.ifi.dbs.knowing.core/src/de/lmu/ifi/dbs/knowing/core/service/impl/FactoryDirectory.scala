package de.lmu.ifi.dbs.knowing.core.service.impl

import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import scala.collection.mutable.HashMap

class FactoryDirectory extends IFactoryDirectory {

  private val factories = new HashMap[String, TFactory]

  def getFactory(id: String): Option[TFactory] = factories.get(id)

  def getFactories(): Array[TFactory] = factories map (_._2) toArray

  def bindFactoryService(service: TFactory) {
    factories.contains(service.id) match {
      case true => println("Factory already exists: " + service.id)
      case false => factories += (service.id -> service)
    }
  }

  def unbindFactoryService(service: TFactory) = factories -= service.id

}