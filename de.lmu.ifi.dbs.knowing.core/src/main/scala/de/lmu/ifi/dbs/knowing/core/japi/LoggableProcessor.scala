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
import akka.event.EventHandler

class LoggableProcessor(processor: TProcessor) {

	def debug(msg: String) = EventHandler.debug(processor, msg)
	def info(msg: String) = EventHandler.info(processor, msg)
	def warning(msg: String) = EventHandler.warning(processor, msg)
	def error(msg: String) = EventHandler.error(processor, msg)
	def error(cause: Throwable) = EventHandler.error(processor, cause)

	def statusChanged(status: Status) = processor.statusChanged(status)
}

object LoggableProcessor {

	def debug(processor: TProcessor, msg: String) = EventHandler.debug(processor, msg)
	def info(processor: TProcessor, msg: String) = EventHandler.info(processor, msg)
	def warning(processor: TProcessor, msg: String) = EventHandler.warning(processor, msg)
	def error(processor: TProcessor, msg: String) = EventHandler.error(processor, msg)
	def error(processor: TProcessor, cause: Throwable) = EventHandler.error(processor, cause)

	def statusChanged(processor: TProcessor, status: Status) = processor.statusChanged(status)
}

/**
 * Provides a hook to the akka-framework for logging
 */
trait ILoggableProcessor {
	def setProcessor(processor: TProcessor)
}