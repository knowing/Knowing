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
import java.util.Properties
import weka.core.{ Instance, Instances }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.processing.IProcessorPorts.{ TRAIN, TEST }
import de.lmu.ifi.dbs.knowing.core.processing.INodeProperties.{ SET_CLASS }
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import java.io.OutputStream
import java.io.InputStream

/**
 *  @author Nepomuk Seiler
 *  @version 0.2
 *  @since 16.06.2011
 */
trait TClassifier extends TProcessor with TSerializable {

	protected var setClass = false

	/**
	 * Distinguish if input should be used to train the classifier
	 * or should be classified and send as results to all connected
	 * nodes.
	 *
	 * @param PartialFunction[Instances, Option[String]] - match on (message, port)
	 */
	override def process(instances: Instances) = {
		case (Some(TEST), None) =>
			guessAndSetClassLabel(instances)
			isBuild match {
				case false => cacheQuery(instances)
				case true =>
					processStoredQueries
					sendResults(query(instances))
			}

		case (Some(TRAIN), None) => build(instances)
		case (None, None) => build(instances)
		case (Some(port), _) => error(this, "Incompatible target port: " + port)
	}

	/**
	 * Default implementation tries to open an input stream
	 * and deserialize an existing classifier. If no inpustream
	 * is found, just a fresh classifier will be started.
	 */
	override def start = inputStream() match {
		case None => debug(this, "Nothing to deserialize in " + getClass.getSimpleName)
		case Some(in) =>
			deserialize(in)
			isBuild = true
	}

	/**
	 * Default implementation tries to open an output stream
	 * to store the internal state of the classifier. If no
	 * output is given, nothing will be stored.
	 */
	override def postStop = outputStream match {
		case None => debug(this, "Nothing to serialize in " + getClass.getSimpleName)
		case Some(out) => serialize(out)
	}

	override def configure(properties: Properties) {
		setClass = properties.getProperty(SET_CLASS, "false").toBoolean
	}

	/**
	 * @param out -> never null nor invalid
	 */
	def serialize(out: OutputStream) = {}

	/**
	 * @param in -> never null nor invalid
	 */
	def deserialize(in: InputStream) = {}

	/**
	 * <p>This method build the internal model which is used<br>
	 * to answer queries.</p>
	 *
	 * <p>The build process should be implemented in an own<br>
	 * thread, so other processors could build up their models<br>
	 * too.</p>
	 *
	 * <p>Calling this method more than once should generate a<br>
	 * new model based on the old one, instead of building a<br>
	 * new model. For reseting the model use {@link #resetModel()}</p>
	 *
	 * @param the dataset
	 */
	def build(instances: Instances)

	/**
	 * <p>The presenter connected to this {@link IResultProcessor} calls this<br>
	 * method to generate his initial presentation model. After that the<br>
	 * presenter starts querying the processor.</p>
	 *
	 * @return - class labels
	 */
	def getClassLabels(): Array[String]

}
