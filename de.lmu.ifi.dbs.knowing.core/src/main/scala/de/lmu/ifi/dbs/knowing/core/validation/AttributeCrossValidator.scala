/*																*\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|	**
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,	**
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|	**
** 																**
** Knowing Framework											**
** Apache License - http://www.apache.org/licenses/				**
** LMU Munich - Database Systems Group							**
** http://www.dbs.ifi.lmu.de/									**
\*																*/
package de.lmu.ifi.dbs.knowing.core.validation

import java.util.Properties
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import de.lmu.ifi.dbs.knowing.core.events._
import weka.core.Instances

/**
 * Splits up the dataset based on a given Attribute
 * and performs a crossvalidation.
 *
 * @author Nepomuk Seiler
 * @version 0.3
 */
class AttributeCrossValidator extends XCrossValidator {

	import AttributeCrossValidator._
	private var splitAttr = ResultsUtil.ATTRIBUTE_SOURCE

	override def process(instances: Instances) = {
		case (None, None) | (Some(DEFAULT_PORT), None) =>
			//Init classlabels
			val index = guessAndSetClassLabel(instances)
			index match {
				case -1 =>
					classLabels = Array()
					log.warning("No classLabel found in " + instances.relationName)
				case x => classLabels = classLables(instances.attribute(x))
			}
			val instMaps = ResultsUtil.splitInstanceByAttribute(instances, splitAttr, false)

			//Map test-data -> train-data 
			val instMap = for (e <- instMaps) yield instMaps.partition(e2 => e._1.equals(e2._1))
			folds = instMap.size
			log.debug("Fold-Actors created!")
			statusChanged(Progress("validation", 0, folds))
			val crossValidators = initCrossValidators(folds)
			var i = 0
			instMap foreach {
				case (test, train) =>
					//Debug purpose
					val sb = new StringBuffer
					sb.append("test[")
					sb.append(test.head._1)
					sb.append("] -> train ")
					train foreach (elem => sb.append(elem._1 + ","))
					log.debug(sb.toString)

					val testData = test.head._2
					val trainData = ResultsUtil.appendInstances(new Instances(testData, 0), train map (_._2) toList)
					guessAndSetClassLabel(testData)
					guessAndSetClassLabel(trainData)
					//Logic
					context.watch(crossValidators(i))
					crossValidators(i) ! Register(self, None)
					crossValidators(i) ! Configure(configureProperties(validator_properties, i))
					crossValidators(i) ! Results(trainData)
					crossValidators(i) ! Query(testData)
					i += 1
			}

			log.debug("Fold-Actors configured and training started")
		case (None, Some(query)) => result(instances, query)
		case (Some(DEFAULT_PORT), Some(query)) => result(instances, query)
	}

	override def configure(properties: Properties) {
		splitAttr = properties.getProperty(ATTRIBUTE, ResultsUtil.ATTRIBUTE_SOURCE)
		super.configure(properties)
	}

}

object AttributeCrossValidator {
	/** Split attribute */
	val ATTRIBUTE = "attribute"
}

class AttributeCrossValidatorFactory extends ProcessorFactory(classOf[AttributeCrossValidator])
