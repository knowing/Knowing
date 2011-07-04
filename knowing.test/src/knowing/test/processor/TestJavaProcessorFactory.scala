package knowing.test.processor

import java.util.Properties
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.japi.JProcessor
import scala.collection.immutable.Map

class TestJavaProcessorFactory extends TFactory {

  val id = TestJavaProcessorFactory.id // id from static field
  val name = TestJavaProcessorFactory.name // name from static field

  // actorOf our Wrapper class defined below
  def getInstance(): ActorRef = actorOf[TestJavaProcessorWrapper]

  def createDefaultProperties(): Properties = new Properties
  def createPropertyValues(): Map[String, Array[_ <: Any]] = Map()
  def createPropertyDescription(): Map[String, String] = Map()
}

/**
 * static fields id and name
 */
object TestJavaProcessorFactory {
  val id = classOf[TestJavaProcessor].getName
  val name = "Test Java Processor"
}

/**
 * Wrapper class
 */
class TestJavaProcessorWrapper extends JProcessor {
  //processor field is abstract and must be implemented
  val processor = new TestJavaProcessor(this)
}