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
package de.lmu.ifi.dbs.knowing.core.weka

import java.util.Properties
import weka.core.{ Instance, Instances }
import weka.filters.Filter
import de.lmu.ifi.dbs.knowing.core.factory.TFactory
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties
import de.lmu.ifi.dbs.knowing.core.processing.TFilter
import de.lmu.ifi.dbs.knowing.core.japi.ILoggableProcessor
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import akka.event.EventHandler.{ debug, info, warning, error }
import akka.actor.UntypedActorFactory

/**
 * Wraps the WEKA Filter class.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 18.06.2011
 *
 */
class WekaFilter(val filter: Filter) extends TFilter {

	/**
	 * <p>Code mainly from weka.filters.Filter</p>
	 */
	override def filter(instances: Instances): Instances = {
		val header = new Instances(instances, 0)
		guessAndSetClassLabel(header)
		filter.setInputFormat(header)
		val results = Filter.useFilter(instances, filter)

		//this resets the RelationLocator
		filter.setInputFormat(header)

		results
	}

	def query(query: Instance): Instances = {
		filter.input(query)
		filter.batchFinished

		val returns = new Instances(filter.getOutputFormat, 1)
		returns.add(filter.output)
		returns
	}

	//TODO override queries

	def result(result: Instances, query: Instance) = {}

	def configure(properties: Properties) = {}

}

/* =========================== */
/* ==== Processor Factory ==== */
/* =========================== */

/**
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.04.2011
 */
class WekaFilterFactory[T <: WekaFilter, S <: Filter](wrapper: Class[T], clazz: Class[S]) extends TFactory {

	val name: String = clazz.getSimpleName
	val id: String = clazz.getName

	def getInstance(): ActorRef = {
		classOf[ILoggableProcessor].isAssignableFrom(clazz) match {
			case false => actorOf(wrapper)
			case true =>
				actorOf {
					val w = wrapper.newInstance
					w.filter.asInstanceOf[ILoggableProcessor].setProcessor(w)
					w
				}
		}
	}

	/* ======================= */
	/* ==== Configuration ==== */
	/* ======================= */

	def createDefaultProperties: Properties = new Properties

	def createPropertyValues: Map[String, Array[_ <: Any]] = Map()

	def createPropertyDescription: Map[String, String] = Map()

}

object WekaFilterFactory {
	val DEBUG = INodeProperties.DEBUG
}