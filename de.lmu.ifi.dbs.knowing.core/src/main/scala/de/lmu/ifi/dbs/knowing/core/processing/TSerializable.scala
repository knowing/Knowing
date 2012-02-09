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

	/**
	 * Generate InputStream from the TProcessor.properties field
	 * with the INodeProperties.DESERIALIZE property.
	 *
	 * Note: After obtaining the InputStream make sure to close
	 * it correctly
	 *
	 * @return Some(InputStream) with opened InputStream or None
	 *
	 */
	def inputStream(): Option[InputStream] = resolveProperty(properties, DESERIALIZE) match {
		//Input successfully created
		case Some(p) if exists(p) => Some(newInputStream(p))
		case _ => properties.getProperty(DESERIALIZE) match {
			case null => None

			case url =>
				var in: InputStream = null
				try {
					val model = new URL(url)
					in = model.openStream
					Some(in)
				} catch {
					case e: MalformedURLException =>
						throwException(e, "Could not deserialize model.")
						None
					case e: IOException =>
						if (in != null) in.close()
						throwException(e, "Could not deserialize model.")
						None
				}
		}
	}

	/**
	 * Generate OutputStream from the TProcessor.properties field
	 * with the INodeProperties.SERIALIZE property. If the destination
	 * already exists it will be overidden.
	 *
	 * Note: After obtaining the OutputStream make sure to close
	 * it correctly
	 *
	 * @return Some(InputStream) with opened OutputStream or None
	 *
	 */
	@throws(classOf[IOException])
	def outputStream(): Option[OutputStream] = resolveProperty(properties, SERIALIZE) match {
		case None => None

		//Input successfully created
		case Some(p) =>
			deleteIfExists(p)
			createDirectories(p.getParent)
			createFile(p)
			Some(newOutputStream(p))
	}

	/**
	 * Generic method to resolve a Path from given properties object.
	 * 
	 * @param properties - the java.util.Properties instance to search for
	 * @param key - the property key which should be used as location target
	 */
	def resolveProperty(properties: Properties, key: String): Option[Path] = {
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

