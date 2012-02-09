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
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import java.util.Properties
import java.io.{ OutputStream, InputStream, IOException }
import java.nio.file.{ Files, Path, Paths, DirectoryStream }
import java.nio.file.Files.{ newInputStream, newOutputStream, newDirectoryStream, deleteIfExists, exists, createDirectories, createFile }
import java.net.{ URI, URL }
import scala.collection.JavaConversions._
import INodeProperties.{ ABSOLUTE_PATH, FILE, FILE_EXTENSIONS, DIR, URL => URL_PROP, EXE_PATH }
import java.nio.file.InvalidPathException

/**
 * <p>Creates Input/OutputStreams based on DPU properties.</p>
 *
 * Override hierarchy for properties looks like this:
 *
 * <li>generic:			Input/OutputStream</li>
 * <li>overridden by:	URL</li>
 * <li>overridden by: 	DIR</li>
 * <li>overridden by: 	FILE</li>
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 2011-11-27
 */
trait TStreamResolver { this: TProcessor =>

	/** If stream has already been created */
	protected var resolved = false

	/** TLoader can handle multiple ipnuts*/
	protected var inputs = Map[String, InputStream]()

	/** */
	protected var outputs = Map[String, OutputStream]()

	/**
	 * Used to hook in the message receive queue.
	 * Handles ConfigureOutput / ConfigureInput events.
	 */
	protected def ioReceive: Receive = {

		case ConfigureOutput(target, output) =>
			if (resolved) warning(this, "Output has already been resolved and will be overriden!")
			resolved = true
			outputs = Map(target -> output)

		case ConfigureInput(source, input) =>
			if (resolved) warning(this, "Input has already been resolved and will be overriden!")
			resolved = true
			inputs = Map(source -> input)
	}

	/*  override def configure(properties: Properties) {
    if (resolved)
      return
      
    inputs = resolveInputs(properties)
    outputs = resolveOutputs(properties)
  }*/

	/**
	 * Resolves all InputStreams described in the given properties.
	 * The returning map structure is [FileName -> InputStream]
	 * Duplicated filenames in different folders cannot be handled.
	 *
	 * @return Map with InputStreams. Empty Map on failure
	 * @throws IOException
	 */
	@throws(classOf[IOException])
	def resolveInputs(properties: Properties): Map[String, InputStream] = {
		resolveFilePath(properties) match {
			case None =>

			//Input successfully created
			case Some(p) =>
				debug(this, "Resolved resource: [" + p.getFileName + "] in [" + p.getParent + "]")
				return Map(p.getFileName.toString -> newInputStream(p))
		}
		resolveDirPath(properties) match {
			case None =>
			case Some(dir) =>
				//Add all sources from directory stream
				var stream: DirectoryStream[Path] = null
				var inputMap = Map[String, InputStream]()
				try {
					val fileExt = properties.getProperty(FILE_EXTENSIONS)
					stream = newDirectoryStream(dir)
					for (file <- stream if file.getFileName.toString.endsWith(fileExt)) {
						debug(this, "Resolved input resource: [" + file.getFileName + "] in [" + dir.normalize + "]")
						inputMap += (file.getFileName.toString -> newInputStream(file))
					}
				} catch {
					case e: IOException => e.printStackTrace()
				} finally {
					stream.close
					//Input successfully created
					return inputMap
				}

		}

		debug(this, "Trying resolve url...")
		resolveInputFromURL(properties) match {
			case None => Map()

			//Input successfully created
			case Some(in) => Map(in._1 -> in._2)
		}
	}

	/**
	 * @return head of resolveInputs(properties) method or None.
	 */
	@throws(classOf[IOException])
	def resolveInput(properties: Properties): Option[InputStream] = resolveInputs(properties).values.headOption

	/**
	 *
	 * @return resolves InputStream described in the given properties
	 */
	@throws(classOf[IOException])
	def resolveOutputs(properties: Properties): Map[String, OutputStream] = {
		resolveFilePath(properties) match {
			case None => Map()
			case Some(f) =>
				debug(this, "Resolved output resource: " + f.getFileName)
				createDirectories(f.getParent)

				//TODO INodeProperties.WRITE_OVERRIDE_EXISTING code here
				Files.exists(f) match {
					case true =>
					case false =>
				}
				deleteIfExists(f)
				createFile(f)
				Map(f.getFileName.toString -> newOutputStream(f))
		}
	}

	@throws(classOf[IOException])
	def resolveOutput(properties: Properties): Option[OutputStream] = resolveOutputs(properties).values.headOption

	/**
	 * @return None - if no absolute Path to the ressource could be created, Some(Path) else
	 */
	protected def resolveFilePath(properties: Properties): Option[Path] = {
		resolveFromFileSystem(properties, FILE, TStreamResolver.acceptAbsoluteFile)
	}

	/**
	 *
	 */
	protected def resolveDirPath(properties: Properties): Option[Path] = {
		resolveFromFileSystem(properties, DIR, TStreamResolver.acceptAbsoluteDir)
	}

	/**
	 * @param properties - INode properties from DPU
	 * @param key - FILE | DIR
	 * @param accept - Accept if path is directory or file
	 * @return None on errors resolving Path, Some(Path) else
	 */
	protected def resolveFromFileSystem(properties: Properties, key: String, accept: (Boolean, Boolean) => Boolean): Option[Path] = {
		TStreamResolver.resolveFromFileSystem(properties, key, accept)
	}

	/**
	 * @return None if property not available, else Some(Filename, InputStream)
	 * @throws IOException - malformed URL,..
	 */
	@throws(classOf[IOException])
	protected def resolveInputFromURL(properties: Properties): Option[(String, InputStream)] = {
		(properties.containsKey(URL_PROP), properties.containsKey(FILE)) match {
			case (false, false) => None

			//Resolve URL from FILE property
			case (false, true) =>
				debug(this, "Trying resolve input via executionPath[" + properties.getProperty(EXE_PATH) + "] and FILE[" + properties.getProperty(FILE) + "]...")
				val execURI = new URI(properties.getProperty(EXE_PATH))
				val fileName = properties.getProperty(FILE)
				val file = Paths.get(execURI).resolve(fileName)
				Some(file.getFileName.toString, Files.newInputStream(file))

			//Resolve URL from URL property
			case (true, _) =>
				debug(this, "Trying resolve input from URL[" + properties.getProperty(URL_PROP) + "]...")
				val url = new URL(properties.getProperty(URL_PROP))
				url.getProtocol match {
					case "file" =>
						val props = new Properties(properties)
						props.setProperty(ABSOLUTE_PATH, "true")
						props.setProperty(FILE, url.getFile)
						resolveFilePath(props) match {
							case None => None
							case Some(p) => Some(p.getFileName.toString, Files.newInputStream(p))
						}
					case _ => Some(url.getFile, url.openStream)
				}
		}
	}
}

/**
 * Static methods for resolving properties from the
 * filesystem or another source.
 *
 * Note: Method calls could only be made inside an actor.
 *
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 2011-11-27
 */
object TStreamResolver {

	val acceptAbsoluteFile = (isAbs: Boolean, isDir: Boolean) => (isAbs, isDir) match {
		//Resolved successfully
		case (true, false) => true
		//Path is a directory!
		case (true, true) => false
		//Really wrong
		case (_, _) => false
	}

	val acceptAbsoluteDir = (isAbs: Boolean, isDir: Boolean) => (isAbs, isDir) match {
		//Resolved successfully
		case (true, true) => true
		//Path is a directory!
		case (true, false) => false
		//Really wrong
		case (_, _) => false
	}

	/**
	 * @param properties - INode properties from DPU
	 * @param key - FILE | DIR
	 * @param accept - Accept if path is directory or file
	 * @return None on errors resolving Path, Some(Path) else
	 */
	def resolveFromFileSystem(properties: Properties, key: String, accept: (Boolean, Boolean) => Boolean): Option[Path] = {
		if (!properties.containsKey(key))
			return None

		debug(this, "Trying resolve [" + key + "]...")
		var path: Path = null
		try {
			path = Paths.get(properties.getProperty(key))
		} catch {
			case e: InvalidPathException =>
				warning(this, "Error resolving from filesystem. " + e.getMessage)
				return None
		}
		val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean

		(path.isAbsolute, absolute) match {

			//Path is absolute and property absolute is selected
			case (true, true) => Some(path)

			//Path isn't absolute, but property assumes it is
			case (false, true) => None

			//Path is relative
			case _ =>
				val execURI = new URI(properties.getProperty(EXE_PATH, ""))
				execURI.getScheme match {
					//Can only handle file schemes
					case "file" =>
						val exePath = Paths.get(execURI)
						val relPath = exePath.resolve(path)
						accept(relPath.isAbsolute, Files.isDirectory(relPath)) match {
							case true => Some(relPath)
							case false => None
						}
					//ResolveInputFromURL will try to resolve this
					case _ => None
				}
		}
	}
}