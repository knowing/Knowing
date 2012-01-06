package knowing.test.processor

import java.util.Properties
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.japi.JProcessor
import scala.collection.immutable.Map

class TestJavaProcessorFactory extends ProcessorFactory(classOf[TestJavaProcessorWrapper]) 

/**
 * Wrapper class
 */
class TestJavaProcessorWrapper extends JProcessor {
  //processor field is abstract and must be implemented
  val processor = new TestJavaProcessor(this)
}