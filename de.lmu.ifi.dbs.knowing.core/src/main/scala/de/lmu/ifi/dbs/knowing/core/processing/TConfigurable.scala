package de.lmu.ifi.dbs.knowing.core.processing

import java.util.Properties

/**
 * Make a class configurable
 */
trait TConfigurable {

  /**
   * Configure this loader. URL, password, file-extension
   * @param properties
   */
  def configure(properties: Properties)
}