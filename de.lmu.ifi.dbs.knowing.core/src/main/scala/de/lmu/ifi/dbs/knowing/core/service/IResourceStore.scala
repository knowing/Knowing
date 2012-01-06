package de.lmu.ifi.dbs.knowing.core.service

import de.lmu.ifi.dbs.knowing.core.model.INode
import java.net.URL

/**
 * Provide access to resources for datamining processes.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait IResourceStore {

  //Service properties
  val LOAD_ALL = "knowing.resourceStore.loadAll"

  /**
   *
   * @param node - INode which needs a resource.
   * @return URI to model or None
   */
  def getResource(node: INode): Option[URL]

  /**
   * @param model - name of resource.
   * @return URI to model or None
   */
  def getResource(resource: String): Option[URL]

}
