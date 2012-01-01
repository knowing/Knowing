package de.lmu.ifi.dbs.knowing.core.test

import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import scala.collection.mutable.HashMap

/**
 * Used for Tests without a OSGi system running.
 * Implements fluent interface.
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-01-01
 */
class EmbeddedFactoryDirectory extends IFactoryDirectory {

  /** factories stored */
  private val factories = HashMap[String, TFactory]()

  def getFactory(id: String): Option[TFactory] = factories.get(id)

  def getFactories(): Array[TFactory] = factories.values toArray

  /**
   * Makes TFactory accessible through this FactoryDirectory
   *  
   * @param factory - factory added to be managed by this directory
   * @throws - on factory null or already managed
   */
  @throws(classOf[IllegalArgumentException])
  def add(factory: TFactory): EmbeddedFactoryDirectory = {
    if (factory == null)
      throw new IllegalArgumentException("Factory cannot be null")

    if (factories.contains(factory.id))
      throw new IllegalArgumentException("Factory with id [" + factory.id + "] + cannot be added twice")
    
    factories += (factory.id -> factory)
    this
  }

}