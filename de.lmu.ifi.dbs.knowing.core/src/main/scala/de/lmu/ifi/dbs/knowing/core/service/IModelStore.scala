package de.lmu.ifi.dbs.knowing.core.service

import de.lmu.ifi.dbs.knowing.core.model.INode
import java.net.URL

/**
 * Service interface for a class which handles default models
 * provided by bundles or repositories.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait IModelStore {

  /**
   *
   * @param node - INode which needs a model
   * @return URI to model or None
   */
  def getModel(node: INode): Option[URL]

  /**
   * @param model - name of model file
   * @return URI to model or None
   */
  def getModel(model: String): Option[URL]
}

/**
 * Constants
 */
object IModelStore {

  val KNOWING_MODEL_MANIFEST_HEADER = "Knowing-DPU-Model"
  val KNOWING_FOLDER = IDPUDirectory.KNOWING_FOLDER
  val MODEL_SEPARATOR = ","
  val MODEL_WILDCARD = "*"

  //Service properties
  val LOAD_ALL = "knowing.modelstore.loadAll"
}