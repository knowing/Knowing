package de.lmu.ifi.dbs.knowing.core.japi

import java.util.Properties
import akka.event.EventHandler
import weka.core.{Instances, Instance}
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor
import de.lmu.ifi.dbs.knowing.core.processing.TSender._
import de.lmu.ifi.dbs.knowing.core.events.Event
import de.lmu.ifi.dbs.knowing.core.processing.TSerializable
import java.io.InputStream
import java.io.OutputStream
import de.lmu.ifi.dbs.knowing.core.events.Status

/**
 * <p>This wrapper class provides a wrapper for processors developed in Java.
 * It just delegates every method calls to the scala API.</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 04.07.2011
 */
abstract class JProcessor extends TProcessor with TSerializable {
  
  val processor: IProcessor
  
  override def start = processor.start
  
  override def postStop = processor.stop
  
  def build(instances: Instances) = processor.build(instances)

  def query(query: Instance): Instances = processor.query(query, self)

  def result(result: Instances, query: Instance) = processor.result(result, query)

  override def messageException(message: Any) = processor.messageException(message)
  
  def configure(properties: Properties) = processor.configure(properties)

  def getInputStream():InputStream = inputStream().getOrElse(null)
  def getOutputStream():OutputStream = outputStream getOrElse(null)
  
  def debug(msg: String) = EventHandler.debug(this, msg)
  def info(msg: String) = EventHandler.info(this, msg)
  def warning(msg: String) = EventHandler.warning(this, msg)
  def error(msg: String) = EventHandler.error(this, msg)
}

