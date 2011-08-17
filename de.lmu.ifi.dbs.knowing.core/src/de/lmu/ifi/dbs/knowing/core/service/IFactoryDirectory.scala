package de.lmu.ifi.dbs.knowing.core.service

import de.lmu.ifi.dbs.knowing.core.factory.TFactory

trait IFactoryDirectory {

  def getFactory(id: String): Option[TFactory]
  
  def getFactories(): Array[TFactory]
}