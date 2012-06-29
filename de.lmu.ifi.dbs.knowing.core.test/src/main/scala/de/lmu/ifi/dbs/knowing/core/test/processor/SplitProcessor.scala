package de.lmu.ifi.dbs.knowing.core.test.processor

import java.util.Properties

import SplitProcessorFactory.OUTPUT1
import SplitProcessorFactory.OUTPUT2
import de.lmu.ifi.dbs.knowing.core.events.Results
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import weka.core.Instance
import weka.core.Instances

class SplitProcessor extends TProcessor {

	def process(inst: Instances) = {
		case _ =>
			//Split up input into two instances
			val toCopy = inst.numInstances / 2
			val one = new Instances(inst, 0, toCopy)
			val two = new Instances(inst, inst.numInstances / 2, toCopy - 1)

			//Sends to OUTPUT1, which is a port
			sendResults(one, Some(OUTPUT1))

			//Sends to OUTPUT2, which is a port too
			sendResults(two, Some(OUTPUT2))
	}

	def query(query: Instances): Instances = throw new UnsupportedOperationException

	def configure(properties: Properties) = {}

}

class SplitProcessorFactory extends ProcessorFactory(classOf[SplitProcessor])

object SplitProcessorFactory {
	val OUTPUT1 = "output1"
	val OUTPUT2 = "output2"
}