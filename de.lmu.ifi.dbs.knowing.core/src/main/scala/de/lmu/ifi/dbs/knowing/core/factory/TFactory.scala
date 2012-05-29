/*                                                              *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
**                                                              **
** Knowing Framework                                            **
** Apache License - http://www.apache.org/licenses/             **
** LMU Munich - Database Systems Group                          **
** http://www.dbs.ifi.lmu.de/                                   **
\*                                                              */
package de.lmu.ifi.dbs.knowing.core.factory

import java.util.Properties
import scala.collection.immutable.HashMap
import scala.collection.JavaConversions._
import akka.actor.{ ActorRef, ActorSystem, Props, ActorContext, ActorPath }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import TFactory.ActorFactory

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

	//TODO realize this with duck-typing: type actorOf..

	def getInstance(factory: ActorFactory): ActorRef

	/* ===================== */
	/* === Configuration === */
	/* ===================== */

	def createDefaultProperties: Properties

	def createPropertyValues: Map[String, Array[_ <: Any]]

	def createPropertyDescription: Map[String, String]
}

object TFactory {

	type ActorFactory = {
		def actorOf(props: Props): ActorRef;
		def actorOf(props: Props, name: String): ActorRef;
		
		def actorFor(path: ActorPath): ActorRef
		def actorFor(path: Iterable[String]): ActorRef
		def actorFor(path: java.lang.Iterable[String]): ActorRef
		def actorFor(name: String): ActorRef
	}

	//Default values for factory properties
	val BOOLEAN_PROPERTY = Array("true", "false")

	//OSGi service properties
	val UI_SYSTEM = "knowing.ui"
	val PROCESSOR_CLASS = "knowing.processor.class"
	val FACTORY_ID = "knowing.factory.id"
	val FACTORY_NAME = "knowing.factory.name"
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

	//TODO try implicit ClassManifest here
	val name = processor.getSimpleName
	val id = processor.getName
	
	//This uses the default ActorSystem
	def getInstance(): ActorRef = getInstance(ActorSystem())

	def getInstance(factory: ActorFactory): ActorRef = factory.actorOf(Props(processor.newInstance))

	def createDefaultProperties: Properties = new Properties

	def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

	def createPropertyDescription: Map[String, String] = Map()
}