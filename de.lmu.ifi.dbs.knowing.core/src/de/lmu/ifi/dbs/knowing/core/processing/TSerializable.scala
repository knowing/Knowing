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

trait TSerializable { this: TProcessor =>

  @throws(classOf[IOException])
  def inputStream(): Option[InputStream] = TSerializable.inputStream(properties)

  @throws(classOf[IOException])
  def outputStream(): Option[OutputStream] = TSerializable.outputStream(properties)
}

object TSerializable {

  val SERIALIZE = INodeProperties.SERIALIZE
  val DESERIALIZE = INodeProperties.DESERIALIZE

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
          case "file" => Some(new FileOutputStream(url.getFile))
          case _ =>
            val con = url.openConnection
            debug(this, "Opend connection: " + con)
            con.setDoOutput(true)
            Some(con.getOutputStream)
        }
    }
  }

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
        Some(u.toURL.openStream)
    }
  }
}