package knowing.test.processor

import java.util.Properties
import de.lmu.ifi.dbs.knowing.core.events.Results
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import weka.core.Instance
import weka.core.Instances
import akka.actor.ActorRef
import akka.actor.Actor
import SplitProcessorFactory._

class SplitProcessor extends TProcessor {

  def build(inst: Instances) {
    val toCopy = inst.numInstances / 2
    val one = new Instances(inst, 0, toCopy)
    val two = new Instances(inst, inst.numInstances / 2, toCopy - 1)
    sendEvent(Results(one), OUTPUT1)
    sendEvent(Results(two), OUTPUT2)
  }

  def query(query: Instance): Instances = { null }

  def result(result: Instances, query: Instance) = {}

  def configure(properties: Properties) = {}

}

class SplitProcessorFactory extends ProcessorFactory(classOf[SplitProcessor])

object SplitProcessorFactory {
  val OUTPUT1 = "output1"
  val OUTPUT2 = "output2"
}