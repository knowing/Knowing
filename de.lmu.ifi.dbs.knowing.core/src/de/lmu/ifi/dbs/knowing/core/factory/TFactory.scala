package de.lmu.ifi.dbs.knowing.core.factory

import java.util.Properties
import scala.collection.immutable.HashMap
import scala.collection.JavaConversions._
import akka.actor.ActorRef

/**
 * <p>This factory creates actors and configurators to create<br>
 * a configuration for your created actor.</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 19.04.2011
 */
trait TFactory {

  val name: String
  val id: String

  protected val properties = createDefaultProperties
  protected val values = createPropertyValues
  protected val description = createPropertyDescription

  def getInstance(): ActorRef

  /* ===================== */
  /* === Configuration === */
  /* ===================== */
  
  def configurator:Configurator = new Configurator(properties, values, description) 
  
  def createDefaultProperties: Properties
  
  def createPropertyValues:Map[String, Array[_<:Any]]
  
  def createPropertyDescription:Map[String, String]
}

object TFactory {
  val boolean_property = Array("true", "false")
}