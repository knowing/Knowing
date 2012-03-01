package knowing.test.processor

import java.util.Properties
import akka.event.EventHandler.{ debug, info, warning, error }
import weka.core.{ Instances, Instance, DenseInstance }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.events._

class EmptyQueryProcessor extends TProcessor {

	def process(instances: Instances) = {
		case _ =>
			debug(this, "Build EmptyQueryProcessor")
			sendEvent(Query(ResultsUtil.emptyResult))
	}

	def query(query: Instances): Instances = throw new UnsupportedOperationException

	def configure(properties: Properties) = {}

}

class EmptyQueryProcessorFactory extends ProcessorFactory(classOf[EmptyQueryProcessor])