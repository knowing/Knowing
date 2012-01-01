package knowing.test.processor

import java.util.Properties

import akka.event.EventHandler.{ debug, info, warning, error }

import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.processing.TSender.DEFAULT_PORT
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import weka.core.Instance
import weka.core.Instances

import MultipleInputsProcessorFactory._

class MultipleInputsProcessor extends TProcessor {

  override def build = {
    case (inst, None) => debug(this, "Default build is called " + inst)
    case (inst, Some(INPUT_TEST)) => debug(this, "INPUT_TEST build is called " + inst)
    case (inst, Some(INPUT_TRAIN)) => debug(this, "INPUT_TRAIN build is called " + inst)
    case (x, y) => warning(this, "Bullshit! " + x.relationName + " / " + y)
  }
  
  def build(instances: Instances) = {  
    warning(this, "This should never be called")
  }

  def query(query: Instance): Instances = { null }

  def result(result: Instances, query: Instance): Unit = {  }

  def configure(properties: Properties): Unit = {  }

}

class MultipleInputsProcessorFactory extends ProcessorFactory(classOf[MultipleInputsProcessor]) {
  
}

object  MultipleInputsProcessorFactory {
  val INPUT_TRAIN = "train"
  val INPUT_TEST = "test"
}