package de.lmu.ifi.dbs.knowing.core.factory

import scala.collection.immutable.HashMap
import scala.collection.JavaConversions._
import java.util.Properties

/**
 * <p>This object is used to create a configuration which is send<br>
 * via the Start(properties) command to the corresponding actor.</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.04.2011
 */
abstract class Configurator(properties: Properties,
		values:Map[String, Array[Any]],
		descriptions:Map[String,String]) {
  
  //TODO Configurator: Better conversion
  def propertyNames: Array[String] = properties.stringPropertyNames().toList.toArray

  def propertyDescription(key: String): String = descriptions(key)

  def propertyValues(key: String): Array[Any] = values(key)
  
  def validate(properties:Properties):Array[String]
}