/*                                                              *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
**                                                              **
** Knowing Framework                                            **
** Apache License - http://www.apache.org/licenses/             **
** LMU Munich - Database Systems Group                          **
** http://www.dbs.ifi.lmu.de/                                   **
\*                                                              */
package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.Actor
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import weka.core.{ Instances, Instance }
import java.util.Properties
import java.net.{ URL, URI }
import java.io.{ FilenameFilter, File, IOException }
import java.nio.file.NoSuchFileException

/**
 * <p>Loader retrieve data from a source and send it into
 * the DPU graph. After a Loader receives the Start messages
 * it begins to load the data.</p>
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait TLoader extends TProcessor with TStreamResolver {

	/**
	 * <p>Override for special behaviour</p>
	 */
	override protected def customReceive = ioReceive orElse loaderReceive

	private def loaderReceive: Receive = {

		case Configure(properties) => loaderConfiguration(properties)

	}

	override def start {
		try {
			val dataset = getDataSet
			sendResults(dataset)
			statusChanged(Finished())
		} catch {
			case e: Exception => throwException(e, "Loading dataset failed")
		}
	}

	/**
	 * <p>This method sends the created dataset to all registered listener</p>
	 *
	 * @return
	 * @throws IOException
	 * @see {@link Loader}
	 */
	@throws(classOf[IOException])
	def getDataSet(): Instances

	private def loaderConfiguration(properties: Properties) {
		configure(properties)
		try {
			if (!resolved) {
				inputs = resolveInputs(properties)
				resolved = true
			}
		} catch {
			case e: NoSuchFileException => throwException(e, "File not available")
			case e: Exception => throwException(e, "Error resolving input")
		}

		statusChanged(Waiting())
	}

	/* == Doesn't needed by TLoader == */
	def process(instances: Instances) = {case _ =>  throw new UnsupportedOperationException("Loader don't accept Results() events. Received " + instances.relationName)}

	def query(instances: Instances): Instances = throw new UnsupportedOperationException("Loader don't accept Query() events. Received " + instances.relationName)


}

object TLoader {

	/* ==== Properties to configure TLoader ==== */
	val ABSOLUTE_PATH = INodeProperties.ABSOLUTE_PATH
	val FILE = INodeProperties.FILE

	/**
	 * [scheme:][//authority][path][?query][#fragment]
	 * default scheme: file
	 */
	val URL = INodeProperties.URL
	val DIR = INodeProperties.DIR
	val FILE_EXTENSIONS = INodeProperties.FILE_EXTENSIONS
	/**
	 * This attribute is added, when using dir-option, so
	 * each source can be identified.
	 *
	 * property values: true | false
	 */
	val SOURCE_ATTRIBUTE = INodeProperties.SOURCE_ATTRIBUTE

	/** Options: single | multiple **/
	val OUTPUT = INodeProperties.OUTPUT
	val OUTPUT_SINGLE = INodeProperties.OUTPUT_SINGLE
	val OUTPUT_MULTIPLE = INodeProperties.OUTPUT_MULTIPLE

	/** Points to the dpu directory. Ends with a file.seperator */
	val EXE_PATH = INodeProperties.EXE_PATH // this properties is created by the GraphSupervisor-Caller

	/**
	 * @return absolute path to inputFile
	 */
	def getFilePath(properties: Properties): String = {
		val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
		absolute match {
			case true => properties.getProperty(FILE)
			case false => getInputURI(properties).getPath
		}
	}

	/**
	 * Tries to resolve URI based on the execution path if
	 * not given as an absolute value.
	 *
	 * @return URI to inputSource
	 */
	def getInputURI(properties: Properties): URI = {
		val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
		val exePath = properties.getProperty(EXE_PATH)
		val file = properties.getProperty(FILE)
		val url = properties.getProperty(URL)
		(url, absolute) match {
			case (null, true) | ("", true) => new URI("file", file, null)
			case (null, false) | ("", false) => resolveFile(exePath, file) getOrElse new URI("file", file, null)
			case (_, true) => new URI(url)
			case (_, false) => resolveFile(exePath, url) getOrElse new URI(url)
			case _ => new URI("")
		}
	}

	/**
	 * Tries to resolve URI based on the execution path if
	 * not given as absolute value. Does this for every file
	 * matching the given file extensions inside the given directory .
	 *
	 * @return URIs to inputSources
	 */
	def getInputURIs(properties: Properties): Array[URI] = {
		val dirPath = properties.getProperty(DIR)
		if (dirPath == null || dirPath.isEmpty)
			return Array(getInputURI(properties))

		val exePath = properties.getProperty(EXE_PATH)
		val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
		val extensions = properties.getProperty(FILE_EXTENSIONS, "").split(',')
		val dir = absolute match {
			case true => new File(dirPath)
			case false =>
				val sep = "/"; //System.getProperty("file.separator")
				// If exe path is path to dpu, remove the dpu-filename
				val lastIndex = exePath.lastIndexOf(sep)
				//Get the directory path
				val exeFile = new URI(exePath.substring(0, lastIndex)).toURL.getFile
				// `.` means the current directory, so this should be removed
				val dirPathNew = dirPath.replace(".", "")
				// Java assumes ./path/ is a file and ./path is a directory
				if (dirPathNew isEmpty) new File(exeFile)
				else new File(exeFile + sep + dirPathNew)
		}
		val files = dir.listFiles(new FilenameFilter {
			def accept(dir: File, name: String): Boolean = extensions filter (ext => name.endsWith(ext)) nonEmpty
		})
		files match {
			case null => Array()
			case _ => files map (_.toURI)
		}
	}

	/**
	 *
	 * @return Some(resolvedURI) or None
	 */
	def resolveFile(exePath: String, filename: String): Option[URI] = {
		exePath match {
			case null | "" => None
			case _ => Some(new URI(exePath).resolve("./" + filename))
		}
	}
}