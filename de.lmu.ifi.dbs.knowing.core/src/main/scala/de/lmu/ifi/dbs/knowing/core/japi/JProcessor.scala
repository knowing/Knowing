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

import weka.core.{ Instances, Instance }
import de.lmu.ifi.dbs.knowing.core.processing.{ TSerializable, TProcessor }
import de.lmu.ifi.dbs.knowing.core.model.IEdge.DEFAULT_PORT
import de.lmu.ifi.dbs.knowing.core.events._
import java.io.{ OutputStream, InputStream, IOException }
import java.net.MalformedURLException
import java.util.Properties

/**
 * <p>This wrapper class provides a wrapper for processors developed in Java.
 * It just delegates every method calls to the scala API.</p>
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 04.07.2011
 */
abstract class JProcessor extends TProcessor with TSerializable {

	/** processor written in Java */
	val processor: IProcessor

	/* ============================================== */
	/* === Delegate methods to the java processor === */
	/* ============================================== */

	override def start = processor.start

	override def postStop = processor.stop

	def process(instances: Instances) = {
		case (port, query) => processor.process(instances, port.getOrElse(null), query.getOrElse(null))
	}

	def query(query: Instances): Instances = processor.query(query, self)

	override def messageException(message: Any) = processor.messageException(message)

	def configure(properties: Properties) = processor.configure(properties)

	/* ============================================== */
	/* ===== Util methods for (de)serialization ===== */
	/* ============================================== */

	@throws(classOf[IOException])
	@throws(classOf[MalformedURLException])
	def getInputStream(): InputStream = inputStream().getOrElse(null)

	@throws(classOf[IOException])
	def getOutputStream(): OutputStream = outputStream getOrElse (null)

	/* ============================================== */
	/* === Logging with akka (slf4j if configured) == */
	/* ============================================== */

	def debug(msg: String) = log.debug(msg)
	def info(msg: String) = log.info(msg)
	def warning(msg: String) = log.warning(msg)
	def error(msg: String) = log.error(msg)
}

