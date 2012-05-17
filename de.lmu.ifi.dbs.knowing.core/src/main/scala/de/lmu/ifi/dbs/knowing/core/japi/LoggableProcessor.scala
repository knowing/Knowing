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
package de.lmu.ifi.dbs.knowing.core.japi

import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.events.Status
import akka.event.Logging

class LoggableProcessor(processor: TProcessor) {

	//TODO LoggableProcessor isn't implemented
	//	val log = Logging(processor.context, processor)

	/*	def debug(msg: String) = log.debug(processor, msg)
	def info(msg: String) = log.info(processor, msg)
	def warning(msg: String) = log.warning(processor, msg)
	def error(msg: String) = log.error(processor, msg)
	def error(cause: Throwable) = log.error(processor, cause)*/

	def debug(msg: String) = println(processor + ":" + msg)
	def info(msg: String) = println(processor + ":" + msg)
	def warning(msg: String) =println(processor + ":" + msg)
	def error(msg: String) = println(processor + ":" + msg)
	def error(cause: Throwable) = println(processor + ":" + cause.getStackTraceString)

	def statusChanged(status: Status) = processor.statusChanged(status)
}

object LoggableProcessor {

	/*	def debug(processor: TProcessor, msg: String) = log.debug(processor, msg)
	def info(processor: TProcessor, msg: String) = log.info(processor, msg)
	def warning(processor: TProcessor, msg: String) = log.warning(processor, msg)
	def error(processor: TProcessor, msg: String) = log.error(processor, msg)
	def error(processor: TProcessor, cause: Throwable) = log.error(processor, cause)*/

	def debug(processor: TProcessor, msg: String) = println(processor + ":" + msg)
	def info(processor: TProcessor, msg: String) = println(processor + ":" + msg)
	def warning(processor: TProcessor, msg: String) = println(processor + ":" + msg)
	def error(processor: TProcessor, msg: String) = println(processor + ":" + msg)
	def error(processor: TProcessor, cause: Throwable) = println(processor + ":" + cause.getStackTraceString)

	def statusChanged(processor: TProcessor, status: Status) = processor.statusChanged(status)
}

/**
 * Provides a hook to the akka-framework for logging
 */
trait ILoggableProcessor {
	def setProcessor(processor: TProcessor)
}