package knowing.test.processor

import java.util.Properties
import akka.event.EventHandler.{debug, info, warning, error}
import weka.core.{Instances, Instance,DenseInstance}
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.events._

class EmptyQueryProcessor extends TProcessor {

  def build(instances: Instances) = { 
    debug(this, "Build EmptyQueryProcessor")
    sendEvent(Query(new DenseInstance(0)))
  }

  def query(query: Instance): Instances = { null }

  def result(result: Instances, query: Instance) = {  }

  def configure(properties: Properties) = {
    
  }

}

class EmptyQueryProcessorFactory extends ProcessorFactory(classOf[EmptyQueryProcessor])