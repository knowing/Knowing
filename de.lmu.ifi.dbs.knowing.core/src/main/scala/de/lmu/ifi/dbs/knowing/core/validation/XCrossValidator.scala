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

import akka.actor.{ ActorSystem, ActorRef, ActorContext, Props }
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.processing.IProcessorPorts.{ TRAIN, TEST }
import de.lmu.ifi.dbs.knowing.core.factory.{ TFactory, ProcessorFactory }
import de.lmu.ifi.dbs.knowing.core.util.{ OSGIUtil, ResultsUtil }
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.exceptions._
import java.util.Properties
import weka.core.{ Instance, Instances, Attribute }
import com.eaio.uuid.UUID

/**
 * Performs a crossvalidation on the given input.
 * Splits the dataset with Instances.train/test.
 * Uses CrossValidator.class for each crossvaldation step.
 *
 * @author Nepomuk Seiler
 * @version 0.3
 */
class XCrossValidator(val factoryDirectory: Option[IFactoryDirectory] = None) extends TProcessor {

	protected var factory: TFactory = _
	protected var folds: Int = _
	protected var validator_properties: Properties = _

	protected var resultHeader: Instances = _
	protected var results: List[Instances] = Nil
	protected var relAttribute = -1
	protected var classLabels: Array[String] = Array()
	protected var currentFold: Int = 0

	private var first_run = true
	private var sortAttribute = ""

	override def customReceive = {
		case status: Status => //statusChanged(status) handle it!
	}

	def process(instances: Instances) = {

		/** Input dataset. Create folds and train CrossValidators */
		case (None, None) | (Some(DEFAULT_PORT), None) =>
			//Init classlabels
			val index = guessAndSetClassLabel(instances)
			index match {
				case -1 =>
					classLabels = Array()
					log.warning("No classLabel found in " + instances.relationName)
				case x => classLabels = classLables(instances.attribute(x))
			}
			//Create crossValidator actors for each fold
			val crossValidators = initCrossValidators(folds)
			log.debug("Fold-Actors created!")
			statusChanged(Progress("validation", 0, folds))
			startCrossValidation(crossValidators, instances)
			log.debug("Fold-Actors configured and training started")

		/** CrossValidator results. Expect ClassDistribution. */
		case (None, Some(query)) => result(instances, query)

		/** CrossValidator results. Expect ClassDistribution. */
		case (Some(DEFAULT_PORT), Some(query)) => result(instances, query)
	}

	def result(result: Instances, query: Instances) {
		//First run - init header and relationalAttribute
		if (resultHeader == null)
			resultHeader = new Instances(result, result.size * folds)

		//===== set class values
		if (result.size != query.size)
			throw new KnowingException("result and query size aren't equal: " + result.size + " != " + query.size)

		//		guessAndSetClassLabel(result)
		guessAndSetClassLabel(query)
		for (i <- 0 until result.size) {
			result.get(i).setClassValue(query.get(i).classValue)
		}

		results = result :: results
		currentFold += 1
		if (currentFold == folds) {
			log.debug("Last Fold " + currentFold + " results arrived")
			sendEvent(Results(mergeResults))
			currentFold = 0
		} else {
			statusChanged(Progress("validation", 1, folds))
			log.debug("Fold " + currentFold + " results arrived")
		}

	}

	protected def initCrossValidators(folds: Int) = for (i <- 0 until folds; val actor = factory.getInstance(context)) yield actor

	protected def startCrossValidation(crossValidators: IndexedSeq[ActorRef], instances: Instances) {
		for (j <- 0 until folds) {
			context.watch(crossValidators(j)) //Start actors and link yourself as supervisor
			crossValidators(j) ! Register(self, None) //Register so results/status events are send to us
			crossValidators(j) ! Configure(configureProperties(validator_properties, j)) //Configure actor
			crossValidators(j) ! Results(instances.trainCV(folds, j), Some(TRAIN)) //Send the train set
			crossValidators(j) ! Results(instances.testCV(folds, j), Some(TEST)) //Query the trained crossValidator instance
		}
	}

	/**
	 * Merge two matrices. Skips zero-values
	 */
	protected def mergeResults: Instances = {
		//Reverse results for ordering with relational attribute
		val returns = ResultsUtil.appendInstances(resultHeader, results.reverse)
		sortAttribute match {
			case null | "" => returns
			case name: String =>
				returns.attribute(name) match {
					case null => log.warning("Attribute " + name + " not available to sort by")
					case a => returns.sort(a)
				}
				returns
			case _ => returns
		}
	}

	def configure(properties: Properties) = {
		factory = new CrossValidatorFactory(factoryDirectory)
		//Set properties for this XCrossValidator
		val strFolds = properties.getProperty(CrossValidatorFactory.FOLDS, "10")
		folds = strFolds.toInt
		validator_properties = properties
		sortAttribute = properties.getProperty(XCrossValidatorFactory.SORT_ATTRIBUTE, "")
	}

	/**
	 * Creates properties for each CrossValidator-fold
	 */
	protected def configureProperties(properties: Properties, fold: Int): Properties = {
		val returns = new Properties
		returns.putAll(properties)
		returns.setProperty(CrossValidatorFactory.FOLD, fold.toString)
		returns.setProperty(CrossValidatorFactory.STANDALONE, "false")
		returns
	}

	def query(query: Instances): Instances = throw new UnsupportedOperationException("XCrossValidator accepts Results() on port " + TRAIN + " and " + TEST)

	def getClassLabels(): Array[String] = classLabels

}

/**
 * @author Nepomuk Seiler
 * @version 0.1
 */
class XCrossValidatorFactory(val factoryDirectory: Option[IFactoryDirectory] = None) extends ProcessorFactory(classOf[XCrossValidator]) {

	override def getInstance(): ActorRef = ActorSystem().actorOf(Props(new XCrossValidator(factoryDirectory)))

	override def getInstance(system: ActorSystem): ActorRef = system.actorOf(Props(new XCrossValidator(factoryDirectory)))

	override def getInstance(context: ActorContext): ActorRef = context.actorOf(Props(new XCrossValidator(factoryDirectory)))

	override def createDefaultProperties: Properties = {
		val props = new Properties();
		props.setProperty(CrossValidatorFactory.CLASSIFIER, "")
		props.setProperty(CrossValidatorFactory.FOLDS, "10")
		props.setProperty(XCrossValidatorFactory.SORT_ATTRIBUTE, "")
		props
	}

}

object XCrossValidatorFactory {

	val name: String = "XCrossValidator"
	val id: String = classOf[XCrossValidator].getName

	val SORT_ATTRIBUTE = "sortAttribute"
}
