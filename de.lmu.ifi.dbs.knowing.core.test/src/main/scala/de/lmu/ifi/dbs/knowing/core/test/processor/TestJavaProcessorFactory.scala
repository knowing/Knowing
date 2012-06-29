package de.lmu.ifi.dbs.knowing.core.test.processor

import java.util.Properties
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.japi.JProcessor
import scala.collection.immutable.Map

class TestJavaProcessorFactory extends ProcessorFactory(classOf[TestJavaProcessorWrapper]) {
  override val id = classOf[TestJavaProcessor].getName
  override val name = "Test Java Processor"
}

/**
 * Wrapper class
 */
class TestJavaProcessorWrapper extends JProcessor {
  //processor field is abstract and must be implemented
  val processor = new TestJavaProcessor(this)
}