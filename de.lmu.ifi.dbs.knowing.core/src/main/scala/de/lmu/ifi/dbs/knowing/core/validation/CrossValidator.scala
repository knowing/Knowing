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

import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.processing.IProcessorPorts.{ TRAIN, TEST }
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil.getFactoryDirectory
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import java.util.Properties
import weka.core.{ Attribute, Instance, Instances }
import scala.collection.mutable.{ Map => MutableMap }
import java.util.ArrayList
import de.lmu.ifi.dbs.knowing.core.service.IFactoryDirectory

/**
 * Single CrossValidation step
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 13.05.2011
 *
 */
class CrossValidator(val factoryDirectory: Option[IFactoryDirectory]) extends TProcessor {

	var filterFactory: TFactory = _
	var classifierFactory: TFactory = _
	var classifierProperties = new Properties

	var folds = 2
	var fold = 0
	var standalone = true

	//TODO flags are not really perfect. Some have double meaning.

	private var resultInstances = MutableMap[Instance, Instances]()
	private var nonZeroValues: Array[Array[Int]] = Array()
	private var classLabels: Array[String] = Array()
	private var classifier: Option[ActorRef] = None
	private var classifierTrained = false
	private var filter: Option[ActorRef] = None
	private var filterTrained = false
	private var queriesFiltered = false

	/** Instances to train classifier with filtered train-data */
	private var testInstances: Instances = _
	
	private var first_run = true

	override def customReceive = {
		case status: Status => statusChanged(status)
	}

	def process(instances: Instances) = {
		case (None, None) | (Some(DEFAULT_PORT), None) | (Some(TRAIN), None) =>
			//    debug(this, "Build CrossValidator " + instances.relationName)
			val index = guessAndSetClassLabel(instances)
			index match {
				case -1 =>
					classLabels = Array()
					warning(this, "No classLabel found in " + instances.relationName)
				case x =>
					classLabels = classLables(instances.attribute(x))
					nonZeroValues = new Array(classLabels.length)
					for (i <- 0 until classLabels.length) nonZeroValues(i) = new Array[Int](classLabels.length).map(_ => 1)
			}
			//Clean up
			(classifier, filter) match {
				case (Some(c), Some(f)) => c stop; f stop
				case (Some(c), None) => c stop
				case (None, None) =>
			}
			//Init filter if available
			filterFactory match {
				case null => filterTrained = true
				case f =>
					filter = Some(filterFactory.getInstance)
					filter.get.dispatcher = self.dispatcher
					self startLink filter.get
					filter.get ! Configure(classifierProperties)
			}

			//Init classifier
			classifier = Some(classifierFactory.getInstance)
			classifier.get.dispatcher = self.dispatcher
			self startLink classifier.get
			classifier.get ! Configure(classifierProperties)

			//Start with filter or directly the classifier
			filterTrained match {
				case true =>
					//No filter, classifier gets trained directly
					debug(this, "Build CrossValidator[unfiltered] with " + instances.relationName)
					classifierTrained = true
					queriesFiltered = true
					startValidation(instances, classifier.get)
				case false =>
					debug(this, "Build CrossValidator[filtered] with" + instances.relationName)
					startValidation(instances, filter.get, true)
			}

		case (Some(TEST), _) => 
			testInstances = instances
			query(instances)

		case (None, Some(q)) => result(instances, q)

		case (Some(DEFAULT_PORT), Some(q)) => result(instances, q)
	}

	/**
	 * Standalone = true => Splits instances, train and test actor
	 * Standalone = false => Trains actor with instances
	 */
	private def startValidation(instances: Instances, processor: ActorRef, filtered: Boolean = false) = standalone match {
		case true =>
			val train = instances.trainCV(folds, fold)
			//Train filter
			processor ! Results(train)
			//Filter training data with trained filter
			if (filtered) { processor ! Query(train) }
			val testSet = instances.testCV(folds, fold)
			processor ! Query(testSet)
		case false =>
			processor ! Results(instances)
			//Filter training data with trained filter
			if (filtered) { processor ! Query(instances) }
	}

	/**
	 * Process results. Merge with confusionMatrix.
	 *
	 * The results identified by the flags classifierTrained, queriesFiltered and
	 * the number of train and test instances have been received.
	 * If no filter is specified, filterTrained and queriesFiltered are automatically true.
	 *
	 * Data flow [unfiltered / no standalone]
	 * [1] classifier ! Results(train)
	 * [2] forward all queries directly to the classifier, without caching. Store numInstancesTest
	 * [3] Receive results, wait for the last instances to arrive, merge and send Results
	 *
	 * Data flow [filtered / no standalone]
	 * [1] filter ! Results(train) -> train filter
	 * [2] filter ! Queries(train) -> filter classifier train data. Store numInstancesTrain
	 * [3] Receive results, wait for last trained instances and than filter testInstances.
	 * [4] Receive results, wait for last trained instances and then query classifier.
	 * [5] Receive results, wait for last classified instance, merge and send Results
	 *
	 *
	 */
	def result(result: Instances, query: Instances) {

		(classifierTrained, queriesFiltered) match {

			// Nothing trained or filtered yet. Must be filtered results.
			case (false, false) =>
				classifier.get ! Results(result)
				filterTrained = true
				classifierTrained = true
				processStoredQueries

			// Classifier was trained with filtered data, filter test data
			case (true, false) =>
				queriesFiltered = true
				classifier.get ! Query(result)

			// Classifier trained and test data filtered
			case (true, true) =>
				// Send Results if currentInst processed is the total numInstances
				sendEvent(Results(result, None, Some(testInstances)))
				statusChanged(Ready())
		}

	}

	/**
	 * <p>Forward query to classifier and process results in result method</p>
	 *
	 * @param query - forwarded to classifier
	 * @returns Instances - ConfusionMatrix
	 */
	def query(query: Instances): Instances = {
		(filter, filterTrained, classifier, classifierTrained, queriesFiltered) match {
			case (_, _, None, _, _) => warning(this, "No classifier found")
			//cache if filtered isn't trained yet
			case (Some(f), false, Some(_), _, _) => cacheQuery(query)
			//forward to filter if exists
			case (Some(f), true, Some(c), false, _) => f ! Query(query)
			case (Some(f), true, Some(c), _, false) => f ! Query(query)
			//forward directly to classifier
			case (_, _, Some(c), true, true) => c ! Query(query)
		}
		null
	}

	/**
	 * Queries stored are forwared to the classifier specified
	 */
	override def processStoredQueries {
		//Does not respect arrival time
		while (queryQueue.nonEmpty) {
			val e = queryQueue.dequeue
			e._1 match {
				case None => //nothing
				case Some(_) => query(e._2.query)
			}
		}
	}

//	private def createHeader(query: Instance, result: Instances): Instances = {
//		//Generate AttributeList
//		val attr = new ArrayList[Attribute](query.numAttributes + result.numAttributes)
//		val qAttr = query.enumerateAttributes
//		while (qAttr.hasMoreElements) {
//			attr.add(qAttr.nextElement.asInstanceOf[Attribute])
//		}
//
//		val ret = new Instances(ResultsUtil.NAME_CLASS_DISTRIBUTION, attr, numInstancesTest)
//		debug(this, "Attributes: " + query.numAttributes + " / " + result.numAttributes)
//		debug(this, "Header: " + ret)
//		guessAndSetClassLabel(ret)
//		ret
//	}

	def getClassLabels(): Array[String] = classLabels

	def configure(properties: Properties) = {
		val cFactoryId = properties.getProperty(CrossValidatorFactory.CLASSIFIER)
		val factoryDir = factoryDirectory.getOrElse(getFactoryDirectory)
		val cFactory = factoryDir.getFactory(cFactoryId)
		cFactory match {
			case Some(f) => classifierFactory = f
			case None => throw new Exception("No Factory with " + cFactoryId + " found!")
		}

		val fFactoryId = properties.getProperty(CrossValidatorFactory.FILTER)
		val fFactory = factoryDir.getFactory(fFactoryId)
		fFactory match {
			case Some(f) => filterFactory = f
			case None => debug(this, "Unfiltered CrossValidation")
		}

		folds = properties.getProperty(CrossValidatorFactory.FOLDS, "10").toInt
		fold = properties.getProperty(CrossValidatorFactory.FOLD, "1").toInt
		standalone = properties.getProperty(CrossValidatorFactory.STANDALONE, "true").toBoolean

		//Remove used properties
		classifierProperties.putAll(properties)
		classifierProperties.remove(CrossValidatorFactory.CLASSIFIER)
		classifierProperties.remove(CrossValidatorFactory.FILTER)
		classifierProperties.remove(CrossValidatorFactory.FOLDS)
		classifierProperties.remove(CrossValidatorFactory.FOLD)
		classifierProperties.remove(CrossValidatorFactory.STANDALONE)
	}

	private def highestProbability(instances: Instances): Int = {
		var index = -1
		var max = -1.0
		val valAttr = instances.attribute(1)
		for (i <- 0 until instances.numInstances) {
			val inst = instances.instance(i)
			val value = inst.value(valAttr)
			if (value > max) {
				max = value
				index = i
			}
		}
		index
	}

}

class CrossValidatorFactory(factoryDirectory: Option[IFactoryDirectory] = None) extends TFactory {

	val name: String = CrossValidatorFactory.name
	val id: String = CrossValidatorFactory.id

	def getInstance(): ActorRef = actorOf(new CrossValidator(factoryDirectory))

	def createDefaultProperties: Properties = {
		val props = new Properties();
		props.setProperty(CrossValidatorFactory.CLASSIFIER, "")
		props.setProperty(CrossValidatorFactory.FILTER, "")
		props.setProperty(CrossValidatorFactory.FOLD, "1")
		props.setProperty(CrossValidatorFactory.FOLDS, "2")
		props
	}

	def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

	def createPropertyDescription: Map[String, String] = Map()
}

object CrossValidatorFactory {

	val name: String = "CrossValidator"
	val id: String = classOf[CrossValidator].getName
	val CLASSIFIER = "classifier"
	val FILTER = "filter"
	val FOLD = "fold"
	val FOLDS = "folds"
	val STANDALONE = "standalone"
}
