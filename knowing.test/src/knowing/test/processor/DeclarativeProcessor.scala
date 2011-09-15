package knowing.test.processor

import akka.event.EventHandler.{ debug, info, warning, error }
import java.util.Properties
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import weka.core.Instance
import weka.core.Instances

import org.osgi.service.component.ComponentContext

class DeclarativeProcessor extends TProcessor {

  def build(instances: Instances) = { 
	  debug(this, "Build for declarative processor")
  }

  def query(query: Instance): Instances = { null }

  def result(result: Instances, query: Instance) = {  }

  def configure(properties: Properties) = {  
    debug(this, "Configure declarative processor")
  }

}

class DeclarativeProcessorFactory extends ProcessorFactory(classOf[DeclarativeProcessor]) {
  
  def activate(context: ComponentContext) {
   println("Declarative Processor Factory activated") 
  }
}