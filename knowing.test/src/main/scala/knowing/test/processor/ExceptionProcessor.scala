package knowing.test.processor

import java.util.Properties
import weka.core.{ Instances, Instance }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory

class ExceptionProcessor extends TProcessor {

  def build(instances: Instances) = {
    throwException(new Exception("BUILDING"), "no details")
  }

  def query(query: Instance): Instances = {
    throwException(new Exception("QUERY"), "no details")
    null
  }

  def result(result: Instances, query: Instance) = {
    throwException(new Exception("RESULT"), "no details")
  }

  def configure(properties: Properties): Unit = {
    throwException(new Exception("CONFIGURE"), "no details")
  }

}

class ExceptionProcessorFactory extends ProcessorFactory(classOf[ExceptionProcessor])