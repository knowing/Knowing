package de.lmu.ifi.dbs.knowing.core.factory

import java.util.Properties
trait TLoaderFactory extends TFactory {

  def getInstance(properties:Properties)
  
}