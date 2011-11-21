package de.lmu.ifi.dbs.knowing.core.factory

import java.util.Properties
import scala.collection.immutable.HashMap
import scala.collection.JavaConversions._
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor

/**
 * <p>This factory should be registered as an OSGi service
 * and will be used to create an instance of the specified
 * actor.
 * </p>
 *
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 19.04.2011
 */
trait TFactory {

  /** human readable name */
  val name: String
  
  /** unique identifier to retrieve this factory */
  val id: String

  protected val properties = createDefaultProperties
  protected val values = createPropertyValues
  protected val description = createPropertyDescription

  /** factory method - creates actor instance */
  def getInstance(): ActorRef

  /* ===================== */
  /* === Configuration === */
  /* ===================== */

  def createDefaultProperties: Properties

  def createPropertyValues: Map[String, Array[_ <: Any]]

  def createPropertyDescription: Map[String, String]
}

object TFactory {
  val BOOLEAN_PROPERTY = Array("true", "false")
}

/**
 * <p>Standard factory with default values for each method:
 * 
 * <li>name: processor.getSimpleName</li>
 * <li>id: processor.getName</li>
 * <li>getInstance: actorOf(processor.newInstance)</li>
 * <li>Properties: empty maps / Properties</li>
 * </p>
 *  
 * @author Nepomuk Seiler
 * @version 1.0
 */
class ProcessorFactory(processor: Class[_ <: TProcessor]) extends TFactory {
  val name = processor.getSimpleName
  val id = processor.getName

  def getInstance(): ActorRef = actorOf(processor.newInstance)

  def createDefaultProperties: Properties = new Properties

  def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

  def createPropertyDescription: Map[String, String] = Map()
}