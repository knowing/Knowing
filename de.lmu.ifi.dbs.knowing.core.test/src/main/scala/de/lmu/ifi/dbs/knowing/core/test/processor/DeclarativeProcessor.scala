package de.lmu.ifi.dbs.knowing.core.test.processor

import java.util.Properties
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import weka.core.Instance
import weka.core.Instances

import org.osgi.service.component.ComponentContext

class DeclarativeProcessor extends TProcessor {

	def process(instances: Instances) = {
		case _ => log.debug("Build for declarative processor")
	}

	def query(query: Instances): Instances = throw new UnsupportedOperationException

	def configure(properties: Properties) = {
		log.debug("Configure declarative processor")
	}

}

class DeclarativeProcessorFactory extends ProcessorFactory(classOf[DeclarativeProcessor]) {

	def activate(context: ComponentContext) {
		println("Declarative Processor Factory activated")
	}
}