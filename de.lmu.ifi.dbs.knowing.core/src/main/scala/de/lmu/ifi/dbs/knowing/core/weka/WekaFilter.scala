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
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties
import de.lmu.ifi.dbs.knowing.core.processing.TFilter
import de.lmu.ifi.dbs.knowing.core.japi.ILoggableProcessor
import akka.actor.{ ActorSystem, ActorContext, ActorRef, UntypedActorFactory, Props }

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
class WekaFilterFactory[T <: WekaFilter, S <: Filter](wrapper: Class[T], clazz: Class[S]) extends ProcessorFactory(wrapper) {

	override val name: String = clazz.getSimpleName
	override val id: String = clazz.getName

	override def getInstance(system: ActorSystem): ActorRef = {
		classOf[ILoggableProcessor].isAssignableFrom(clazz) match {
			case false => system.actorOf(Props(wrapper.newInstance))
			case true =>
				system.actorOf(Props {
					val w = wrapper.newInstance
					w.filter.asInstanceOf[ILoggableProcessor].setProcessor(w)
					w
				})
		}
	}

	override def getInstance(context: ActorContext): ActorRef = {
		classOf[ILoggableProcessor].isAssignableFrom(clazz) match {
			case false => context.actorOf(Props(wrapper.newInstance))
			case true =>
				context.actorOf(Props {
					val w = wrapper.newInstance
					w.filter.asInstanceOf[ILoggableProcessor].setProcessor(w)
					w
				})
		}
	}

}

object WekaFilterFactory {
	val DEBUG = INodeProperties.DEBUG
}