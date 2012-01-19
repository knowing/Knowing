package de.lmu.ifi.dbs.knowing.core.processing

import akka.actor.Actor
import akka.event.EventHandler.{ debug, info, warning, error }
import java.util.Properties
import java.io.{ OutputStream, InputStream, IOException }
import java.net.{ URI, URL }
import java.nio.file.{ Files, Path, Paths, DirectoryStream }
import java.nio.file.Files.{ newInputStream, newOutputStream, exists, deleteIfExists, createDirectories, createFile }
import scala.collection.JavaConversions._
import INodeProperties.{ SERIALIZE, DESERIALIZE }
import TLoader._
import java.net.MalformedURLException

/**
 * <p>Makes a TProcessor serialiazable</p>
 *
 * @author Nepomuk Seiler
 * @version 0.2
 */
trait TSerializable extends TStreamResolver { this: TProcessor =>

  @throws(classOf[IOException])
  @throws(classOf[MalformedURLException])
  def inputStream(): Option[InputStream] = resolveSerializeProperty(properties, DESERIALIZE) match {
    //Input successfully created
    case Some(p) if exists(p) => Some(newInputStream(p))
    case _ => properties.getProperty(DESERIALIZE) match {
      case null => None

      case url => try {
        val model = new URL(url)
        Some(model.openStream)
      } catch {
        case e: MalformedURLException =>
          throwException(e, "Could not deserialize model")
          None
      }
    }
  }

  @throws(classOf[IOException])
  def outputStream(): Option[OutputStream] = resolveSerializeProperty(properties, SERIALIZE) match {
    case None => None

    //Input successfully created
    case Some(p) =>
      deleteIfExists(p)
      createDirectories(p.getParent)
      createFile(p)
      Some(newOutputStream(p))
  }

  def resolveSerializeProperty(properties: Properties, key: String): Option[Path] = {
    val accept = (isAbs: Boolean, isDir: Boolean) => (isAbs, isDir) match {
      //Resolved successfully
      case (true, false) => true
      //Path is a directory!
      case (true, true) => false
      //Really wrong
      case (_, _) => false
    }
    resolveFromFileSystem(properties, key, accept)
  }
}

