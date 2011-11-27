package de.lmu.ifi.dbs.knowing.core.processing

import akka.event.EventHandler.{ debug, info, warning, error }
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil
import java.util.Properties
import java.io.{ OutputStream, InputStream, IOException }
import java.nio.file.{ Files, Path, Paths, DirectoryStream }
import java.nio.file.Files.{ newInputStream, newOutputStream, newDirectoryStream, deleteIfExists, createDirectories, createFile }
import java.net.{ URI, URL }
import scala.collection.JavaConversions._
import INodeProperties.{ ABSOLUTE_PATH, FILE, FILE_EXTENSIONS, DIR, URL => URL_PROP, EXE_PATH }

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
 * @version 0.1
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
   *
   * @return array with InputStreams. Empty Array on failure
   * @throws IOException
   */
  @throws(classOf[IOException])
  def resolveInputs(properties: Properties): Map[String, InputStream] = {
    //Try 
    resolveFilePath(properties) match {
      case None =>

      //Input successfully created
      case Some(p) => return Map(p.getFileName.toString -> newInputStream(p))
    }

    resolveDirPath(properties) match {
      case None =>
      case Some(p) =>
        //Add all sources from directory stream
        var stream: DirectoryStream[Path] = null
        var inputMap = Map[String, InputStream]()
        try {
          val fileExt = properties.getProperty(FILE_EXTENSIONS)
          stream = newDirectoryStream(p)
          for (f <- stream if f.getFileName.endsWith(fileExt)) {
            inputMap += (f.getFileName.toString -> newInputStream(f))
          }
        } catch {
          case e: IOException => e.printStackTrace()
        } finally {
          stream.close
          //Input successfully created
          return inputMap
        }

    }

    resolveInputFromURL(properties) match {
      case None => Map()

      //Input successfully created
      case Some(in) => Map(ResultsUtil.UNKOWN_SOURCE -> in)
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
      case Some(p) =>
        createDirectories(p.getParent)
        deleteIfExists(p)
        createFile(p)
        Map(p.getFileName.toString -> newOutputStream(p))
    }
  }

  @throws(classOf[IOException])
  def resolveOutput(properties: Properties): Option[OutputStream] = resolveOutputs(properties).values.headOption

  /**
   * @return None - if no absolute Path to the ressource could be created, Some(Path) else
   */
  private def resolveFilePath(properties: Properties): Option[Path] = {
    val accept = (isAbs: Boolean, isDir: Boolean) => (isAbs, isDir) match {
      //Resolved successfully
      case (true, false) => true
      //Path is a directory!
      case (true, true) => false
      //Really wrong
      case (_, _) => false
    }
    resolveFromFileSystem(properties, FILE, accept)
  }

  /**
   *
   */
  private def resolveDirPath(properties: Properties): Option[Path] = {
    val accept = (isAbs: Boolean, isDir: Boolean) => (isAbs, isDir) match {
      //Resolved successfully
      case (true, true) => true
      //Path is a directory!
      case (true, false) => false
      //Really wrong
      case (_, _) => false
    }
    resolveFromFileSystem(properties, DIR, accept)
  }

  /**
   * @param properties - INode properties from DPU
   * @param key - FILE | DIR
   * @param accept - Accept if path is directory or file
   * @return None on errors resolving Path, Some(Path) else
   */
  private def resolveFromFileSystem(properties: Properties, key: String, accept: (Boolean, Boolean) => Boolean): Option[Path] = {
    if (!properties.containsKey(key))
      return None

    val path = Paths.get(properties.getProperty(key))
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

  /**
   * @return None if property not available, else Some(InputStream)
   * @throws IOException - malformed URL,..
   */
  @throws(classOf[IOException])
  private def resolveInputFromURL(properties: Properties): Option[InputStream] = {
    (properties.containsKey(URL_PROP), properties.containsKey(FILE)) match {
      case (false, false) => None

      //Resolve URL from FILE property
      case (false, true) =>
        val execURI = new URI(properties.getProperty(EXE_PATH, ""))
        val fileName = properties.getProperty(FILE)
        val file = execURI.resolve(fileName)
        Some(file.toURL.openStream)

      //Resolve URL from URL property
      case (true, _) =>
        val url = new URL(properties.getProperty(URL_PROP))
        url.getProtocol match {
          case "file" =>
            val props = new Properties(properties)
            props.setProperty(ABSOLUTE_PATH, "true")
            props.setProperty(FILE, url.getFile)
            resolveFilePath(props) match {
              case None => None
              case Some(p) => Some(Files.newInputStream(p))
            }
          case _ => Some(url.openStream)
        }
    }
  }
}