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
package de.lmu.ifi.dbs.knowing.core.processing

import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.TSaver._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import java.util.Properties
import weka.core.{ Instances, Instance }
import java.io.IOException

/**
 * OutputProcessor
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait TSaver extends TProcessor with TStreamResolver {

	protected var _mode = WRITE_MODE_NONE
	protected var _file = "<no file>"
	protected var _url = "<no url>"

	/**
	 * <p>Override for special behaviour</p>
	 */
	override protected def customReceive = ioReceive orElse saverReceive

	private def saverReceive: Receive = {

		case Configure(p) => saverConfiguration(p)

		case Reset() => reset
	}

	@throws(classOf[IOException])
	def write(instances: Instances)

	/**
	 * Reset dataset, properties and possible connections, inputstreams, etc.
	 */
	def reset

	private def saverConfiguration(properties: Properties) {
		_mode = properties.getProperty(WRITE_MODE, WRITE_MODE_INCREMENTAL)
		_file = properties.getProperty(FILE, "<no file>")
		_url = properties.getProperty(URL, "<no url>")
		configure(properties)
		if (!resolved) {
			outputs = resolveOutputs(properties)
			resolved = true
		}
		statusChanged(Waiting())
	}

	/**
	 * <p>Just delegating to write(instances)</p>
	 */
	def process(instances: Instances) = {
		case _ => write(instances)
	}

	/* === Getter Methods === */
	def url: String = _url

	def file: String = _file

	def fetchMode: String = _mode

	def isBatch = _mode equals (WRITE_MODE_BATCH)

	def isIncremental = _mode equals (WRITE_MODE_INCREMENTAL)

	/* == Doesn't needed by TSaver == */
	def query(instance: Instances): Instances = throw new UnsupportedOperationException("Saver implementation doesn't support query")

}

object TSaver {
	/* ==== Properties to configure TSaver ==== */
	val ABSOLUTE_PATH = INodeProperties.ABSOLUTE_PATH
	val FILE = INodeProperties.FILE
	val URL = INodeProperties.URL

	val WRITE_MODE = INodeProperties.WRITE_MODE
	val WRITE_MODE_NONE = INodeProperties.WRITE_MODE_NONE
	val WRITE_MODE_BATCH = INodeProperties.WRITE_MODE_BATCH
	val WRITE_MODE_INCREMENTAL = INodeProperties.WRITE_MODE_INCREMENTAL

	def getFilePath(properties: Properties): String = TLoader.getFilePath(properties)
}