package de.lmu.ifi.dbs.knowing.core.service

import de.lmu.ifi.dbs.knowing.core.factory.TFactory

/**
 * Directory for all registered TFactory services.
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait IFactoryDirectory {

  /**
   * @return Some(factory) else None
   */
  def getFactory(id: String): Option[TFactory]
  
  //Check for java compatibility
  //def apply(id: String): Option[TFactory] = getFactory(id)
  
  /**
   * @return all registered TFactory services
   */
  def getFactories(): Array[TFactory]
}