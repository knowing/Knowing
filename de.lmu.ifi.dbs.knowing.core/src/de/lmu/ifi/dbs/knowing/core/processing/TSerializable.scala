package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.Actor
import akka.event.EventHandler.{ debug, info, warning, error }
import java.util.Properties
import java.io.InputStream
import java.io.OutputStream
import java.net.{ URI, URL }
import java.io.IOException
import java.io.FileOutputStream

import TSerializable._
import TLoader._

/**
 * <p>Makes a TProcessor serialiazable</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
trait TSerializable { this: TProcessor =>

  @throws(classOf[IOException])
  def inputStream(): Option[InputStream] = TSerializable.inputStream(properties)

  @throws(classOf[IOException])
  def outputStream(): Option[OutputStream] = TSerializable.outputStream(properties)
}

/**
 * Contains util methods for TSerializable
 */
object TSerializable {

  val SERIALIZE = INodeProperties.SERIALIZE
  val DESERIALIZE = INodeProperties.DESERIALIZE

  /**
   * Generates the output stream based on the following properties:
   * <li>SERIALIZE</li>
   * <li>EXE_PATH</li>
   * <li>ABSOLUTE_PATH</li>
   */
  @throws(classOf[IOException])
  def outputStream(properties: Properties): Option[OutputStream] = {
    val file = properties.getProperty(SERIALIZE, "<empty>")
    val exePath = properties.getProperty(EXE_PATH, "")
    val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
    //Resolve uri, absolute, relative or not set
    val uri = (file, absolute) match {
      case ("<empty>", _) => None
      case (f, true) => Some(new URI(f))
      case _ => resolveFile(exePath, file)
    }
    uri match {
      case None => None
      case Some(u) =>
        val url = u.toURL
        debug(this, "URL: " + url)
        url.getProtocol match {
          
          //Handle file explicit 
          case "file" => Some(new FileOutputStream(url.getFile))
          
          // This must be tested - Doesn't work for TCP connections (no write access)
          case _ =>
            val con = url.openConnection
            debug(this, "Opend connection: " + con)
            con.setDoOutput(true)
            Some(con.getOutputStream)
        }
    }
  }

    /**
   * Generates the input stream based on the following properties:
   * <li>SERIALIZE</li>
   * <li>EXE_PATH</li>
   * <li>ABSOLUTE_PATH</li>
   */
  @throws(classOf[IOException])
  def inputStream(properties: Properties): Option[InputStream] = {
    val file = properties.getProperty(DESERIALIZE, "<empty>")
    val exePath = properties.getProperty(EXE_PATH, "")
    val absolute = properties.getProperty(ABSOLUTE_PATH, "false").toBoolean
    val uri = (file, absolute) match {
      case ("<empty>", _) => None
      case (f, true) => Some(new URI(f))
      case _ => resolveFile(exePath, file)
    }
    uri match {
      case None => None
      case Some(u) =>
        debug(this, "Resolved URI: " + u)
        try {
          Some(u.toURL.openStream)
        } catch {
          case e: IOException => warning(this, e.getMessage); None
        }
        
    }
  }
}