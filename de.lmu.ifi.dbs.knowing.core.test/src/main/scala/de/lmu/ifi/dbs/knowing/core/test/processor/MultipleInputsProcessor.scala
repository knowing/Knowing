package de.lmu.ifi.dbs.knowing.core.test.processor

import java.util.Properties

import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.model.IEdge.DEFAULT_PORT
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import weka.core.Instance
import weka.core.Instances

import MultipleInputsProcessorFactory._

class MultipleInputsProcessor extends TProcessor {

	def process(instances: Instances) = {
		case (None, _) => log.debug("Default build is called " + instances)
		case (Some(INPUT_TEST), _) => log.debug("INPUT_TEST build is called " + instances)
		case (Some(INPUT_TRAIN), _) => log.debug("INPUT_TRAIN build is called " + instances)
		case (x, y) => log.warning("Bullshit! " + instances.relationName + " / " + y)
	}

	def query(query: Instances): Instances = throw new UnsupportedOperationException

	def configure(properties: Properties) = {}

}

class MultipleInputsProcessorFactory extends ProcessorFactory(classOf[MultipleInputsProcessor]) {

}

object MultipleInputsProcessorFactory {
	val INPUT_TRAIN = "train"
	val INPUT_TEST = "test"
}