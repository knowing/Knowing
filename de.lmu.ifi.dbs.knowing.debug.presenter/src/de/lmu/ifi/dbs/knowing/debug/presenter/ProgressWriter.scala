/*                                                               *\
 ** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
 ** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
 ** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
 **                                                              **
 ** Knowing Framework                                            **
 ** Apache License - http://www.apache.org/licenses/             **
 ** LMU Munich - Database Systems Group                          **
 ** http://www.dbs.ifi.lmu.de/                                   **
\*                                                               */
package de.lmu.ifi.dbs.knowing.debug.presenter

import java.io.{PrintWriter, Writer}
import akka.actor.ActorRef
import de.lmu.ifi.dbs.knowing.core.events.Status

/**
 * Writes out status updates send to the UI factory.
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-05-01
 * @see ProgressReader
 */
class ProgressWriter(w: Writer) extends PrintWriter(w) {

	def write(actor: ActorRef, status: Status) {
		val clazz = actor.path
		write(clazz + ";" + status + "\n")
	}
	
}