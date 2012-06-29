/*                                                              *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
**                                                              **
** Knowing Framework                                            **
** Apache License - http://www.apache.org/licenses/             **
** LMU Munich - Database Systems Group                          **
** http://www.dbs.ifi.lmu.de/                                   **
\*                                                              */
package de.lmu.ifi.dbs.knowing.core.test.processor

import java.util.Properties
import weka.core.Instances
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.{ TClassPropertyResolver, TProcessor }
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import ClassPropertyProcessorFactory._

class ClassPropertyProcessor extends TProcessor with TClassPropertyResolver {

	override def preStart() {
		self ! Results(null) //To finish process
	}
	
	def process(instances: Instances) = {
		case (port, query) =>
	}

	def query(query: Instances): Instances = { null }

	def configure(properties: Properties) = {
		val param1 = resolveClass[ParameterClass1](PARAM_CLASS_ONE)
		log.debug(param1.toString)
		val param2 = resolveClass[ParameterClass2](PARAM_CLASS_TWO)
		log.debug(param2.toString)
		log.debug(param2.get.precision + " / " + param2.get.descr)
	}

}

class ClassPropertyProcessorFactory extends ProcessorFactory(classOf[ClassPropertyProcessor]) {}

object ClassPropertyProcessorFactory {
	val PARAM_CLASS_ONE = "paramClassOne"
	val PARAM_CLASS_TWO = "paramClassTwo"
}

class ParameterClass1

class ParameterClass2(val precision: Double, val descr: String)