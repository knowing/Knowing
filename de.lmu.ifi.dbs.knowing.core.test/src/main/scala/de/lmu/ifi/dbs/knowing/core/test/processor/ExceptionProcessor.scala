package de.lmu.ifi.dbs.knowing.core.test.processor

import java.util.Properties
import weka.core.{ Instances, Instance }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory

class ExceptionProcessor extends TProcessor {

	def process(instances: Instances) = {
		case _ => throwException(new Exception("BUILDING"), "no details")
	}

	def query(query: Instances): Instances = {
		throwException(new Exception("QUERY"), "no details")
		null
	}

	def configure(properties: Properties): Unit = {
		throwException(new Exception("CONFIGURE"), "no details")
	}

}

class ExceptionProcessorFactory extends ProcessorFactory(classOf[ExceptionProcessor])